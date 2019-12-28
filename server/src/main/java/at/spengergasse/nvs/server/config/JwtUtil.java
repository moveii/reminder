package at.spengergasse.nvs.server.config;

import at.spengergasse.nvs.server.dto.UserDto;
import at.spengergasse.nvs.server.model.AuthToken;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Date;
import java.util.function.Function;

import static at.spengergasse.nvs.server.model.Constants.*;

/**
 * This class provides util methods for validating and handling JWTs.
 */

@Slf4j
@Component
public class JwtUtil {

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

    /**
     * Returns the claim (determined by the function) from the given token.
     *
     * @param token          the token
     * @param claimsResolver the function
     * @param <T>            the type
     * @return the claim from the token
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Validates the token. Needs {@code UserDetails} for this operation. Returns true if the token is valid, false
     * otherwise.
     *
     * @param token       the token to be validated
     * @param userDetails the currently used {@code UserDetails} object
     * @return true if the token is valid, false otherwise.
     * @see JwtUtil#isTokenExpired
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (
                username.equals(userDetails.getUsername())
                        && !isTokenExpired(token));
    }

    /**
     * Returns all claims associated with the token.
     *
     * @param token the token
     * @return all claims associated with the token
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Returns true if the token is expired. False otherwise.
     *
     * @param token the token
     * @return true if the token is expired. False otherwise
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Generates a new token associated to the username in the {@code UserDto} object.
     *
     * @param userDto the {@code UserDto}
     * @return the token associated to the username in the {@code UserDto} object
     * @see JwtUtil#doGenerateToken
     */
    public String generateToken(UserDto userDto) {
        return doGenerateToken(userDto.getUsername());
    }

    /**
     * Generates a new token associated to the username.
     *
     * @param username the username to be associated with the token
     * @return the token associated to the given username
     */
    private String doGenerateToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);
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
