package at.spengergasse.nvs.server.service;

import at.spengergasse.nvs.server.dto.UserDto;
import at.spengergasse.nvs.server.model.User;
import at.spengergasse.nvs.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public void registerUser(UserDto userDto) {
        User user = generateUserFromDto(userDto);

        userRepository.save(user);
    }

    public boolean authenticateUser(UserDto userDto) {
        User user = generateUserFromDto(userDto);

        return userRepository
                .findUserByUsernameAndPassword(user.getUsername(), user.getPassword())
                .isPresent();
    }

    private User generateUserFromDto(UserDto userDto) {
        return User
                .builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .build();
    }
}
