package at.spengergasse.nvs.server.service;

import at.spengergasse.nvs.server.dto.UpdateUserDto;
import at.spengergasse.nvs.server.dto.UserDto;
import at.spengergasse.nvs.server.model.User;
import at.spengergasse.nvs.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Contains all service methods for the authentication and user based use cases.
 */

@RequiredArgsConstructor
@Service(value = "userService")
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bcryptEncoder;

    /**
     * Loads the user by it's name and returns the corresponding {@code UserDetails}.
     *
     * @param username the username of the user
     * @return the corresponding UserDetails
     */
    public UserDetails loadUserByUsername(String username) {
        Optional<User> userOptional = findByUsername(username);
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("Invalid username or password."));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthority());
    }

    /**
     * Finds the user by it's name and returns it as an optional.
     *
     * @param username the username to find
     * @return the user wrapped in an optional
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findById(username);
    }

    /**
     * Creates a new user by the information given as a {@code UserDto}. Encrypts the password and saves the data in the
     * database.
     *
     * @param userDto the {@code UserDto} from which the user shall be created
     * @return the {@code UserDto} containing all the information
     */
    public UserDto createUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(bcryptEncoder.encode(userDto.getPassword()));
        User savedUser = userRepository.save(user);

        return UserDto
                .builder()
                .username(savedUser.getUsername())
                .build();
    }

    /**
     * Updates the user's password.
     *
     * @param updateUserDto the data necessary to update the user's password
     * @return the new {@code User} object as an {@code UserDto} wrapped in an {@code Optional}.
     */
    public Optional<UserDto> updateUserPassword(UpdateUserDto updateUserDto) {
        Optional<User> userOptional = findByUsername(updateUserDto.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (bcryptEncoder.matches(updateUserDto.getOldPassword(), user.getPassword())) {
                user.setPassword(bcryptEncoder.encode(updateUserDto.getNewPassword()));
                User savedUser = userRepository.save(user);

                return Optional.of(UserDto.builder().username(savedUser.getUsername()).build());
            }
        }

        return Optional.empty();
    }

    /**
     * Returns the authorities. Currently there is only one role, because role based authentication is not needed.
     *
     * @return a list of roles
     */
    private List<SimpleGrantedAuthority> getAuthority() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
