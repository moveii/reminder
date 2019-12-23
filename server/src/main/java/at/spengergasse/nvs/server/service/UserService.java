package at.spengergasse.nvs.server.service;

import at.spengergasse.nvs.server.dto.UserDto;
import at.spengergasse.nvs.server.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> findByUsername(String username);

    UserDto save(UserDto user);

    List<User> findAll();

    void delete(String username);

    UserDto update(UserDto userDto);
}
