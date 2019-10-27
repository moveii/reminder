package at.spengergasse.nvs.server.controller;

import at.spengergasse.nvs.server.dto.UserDto;
import at.spengergasse.nvs.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(path = "register")
    public void registerUser(@RequestBody UserDto userDto) {
        userService.registerUser(userDto);
    }

    @PostMapping
    public ResponseEntity authenticateUser(@RequestBody UserDto userDto) {
        boolean authenticated = userService.authenticateUser(userDto);

        HttpStatus httpStatus = authenticated ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;

        //TODO implement token store and sessions

        return ResponseEntity
                .status(httpStatus)
                .build();
    }
}
