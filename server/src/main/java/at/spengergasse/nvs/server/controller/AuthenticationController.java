package at.spengergasse.nvs.server.controller;

import at.spengergasse.nvs.server.config.JwtTokenUtil;
import at.spengergasse.nvs.server.dto.UserDto;
import at.spengergasse.nvs.server.model.AuthToken;
import at.spengergasse.nvs.server.model.User;
import at.spengergasse.nvs.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/**
 * This class provides all methods necessary for the REST-API for authentication.
 */

@Slf4j
@RestController
@RequestMapping(path = "/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    /**
     * Authenticates the user with the given user. If successful, a token will be generated and returned. Otherwise,
     * an {@code AuthenticationException} will be thrown.
     *
     * @param user the user to be logged in
     * @return the {@code AuthToken} containing the authentication token
     * @throws AuthenticationException when the user cannot be authenticated
     */
    @PostMapping(value = "/login")
    public ResponseEntity<AuthToken> login(@RequestBody @Valid UserDto user) throws AuthenticationException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        final String token = jwtTokenUtil.generateToken(user);
        return ResponseEntity.ok(new AuthToken(token, user.getUsername()));
    }


    /**
     * Creates a new user with the given data. If the username exists, a {@code ResponseEntity} with the HTTP-Status 409
     * will be returned. Otherwise, the created user will be returned.
     *
     * @param user the user to be created
     * @return the created user or HTTP-Status 409 if username exists
     */
    @PostMapping(value = "/register")
    public ResponseEntity<UserDto> register(@RequestBody @Valid UserDto user) {
        Optional<User> userOptional = userService.findByUsername(user.getUsername());
        if (userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            return ResponseEntity.ok(userService.createUser(user));
        }
    }

    /**
     * Deletes the username by the given name.
     *
     * @param username the name of the user to be deleted
     * @return a {@code ResponseEntity} with HTTP-Status 200 if user is deleted
     */
    @DeleteMapping("/user/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userService.delete(username);
        return ResponseEntity.ok().build();
    }
}
