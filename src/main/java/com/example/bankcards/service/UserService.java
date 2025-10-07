package com.example.bankcards.service;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;

import java.util.List;

public interface UserService {

    User findByUsername(String username);

    User getCurrentUser();

    UserDto createUser(UserDto userDto);

    User save(UserDto user);

    User blockUser(Long id);

    User unlockUser(Long id);

    void deleteUser(Long id);

    List<User> getAllUsers();
}
