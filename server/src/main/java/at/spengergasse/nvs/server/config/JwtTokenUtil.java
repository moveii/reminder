package at.spengergasse.nvs.server.config;

import at.spengergasse.nvs.server.dto.UserDto;
import at.spengergasse.nvs.server.model.AuthToken;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.function.Function;

import static at.spengergasse.nvs.server.model.Constants.*;

@Slf4j
@Component
public class JwtTokenUtil implements Serializable {

    /**
     * Extracts the token from the request, gets the associated user and returns the username.
     *
     * @param httpServletRequest the request in which the token is contained
     * @return the username linked to the token in the request
     */
    public String getUsernameFromRequest(HttpServletRequest httpServletRequest) {
        AuthToken tokenFromRequest = getTokenFromRequest(httpServletRequest);
        return tokenFromRequest.getUsername();
    }

    /**
     * Extracts the token from the request, gets the associated user and returns an {@code AuthToken}.
     *
     * @param request the request in which the token is contained
     * @return the {@code AuthToken} containing the username and token
     */
    public AuthToken getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(HEADER_STRING);
        String username = null;
        String authToken = null;

        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            authToken = header.replace(TOKEN_PREFIX, "");
            try {
                username = getUsernameFromToken(authToken);
            } catch (IllegalArgumentException e) {
                log.error("An error occurred during getting username from token");
            } catch (ExpiredJwtException e) {
                log.warn("The token is expired and not valid anymore");
            } catch (SignatureException e) {
                log.error("Authentication Failed. Username or Password not valid.");
            }
        }

        return new AuthToken(authToken, username);
    }

    /**
     * Returns the associated username to the token.
     *
     * @param token the token
     * @return the username associated to the token
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Returns the expiration date of the token.
     *
     * @param token the token
     * @return the expiration date of the token
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (
                username.equals(userDetails.getUsername())
                        && !isTokenExpired(token));
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(UserDto userDto) {
        return doGenerateToken(userDto.getUsername());
    }

    private String doGenerateToken(String subject) {
        Claims claims = Jwts.claims().setSubject(subject);
        claims.put("scopes", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("backend")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
                .signWith(SignatureAlgorithm.HS384, SIGNING_KEY)
                .compact();
    }
}
