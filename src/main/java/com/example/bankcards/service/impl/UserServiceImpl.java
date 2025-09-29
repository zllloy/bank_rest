package com.example.bankcards.service.impl;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.RoleNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с именем '" + username + "' не найден"));
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    @Transactional
    public User createUser(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RoleNotFoundException("Роль USER не найдена"));

        User newUser = User.builder()
                .username(userDto.getUsername())
                .password(encoder.encode(userDto.getPassword()))
                .email(userDto.getEmail())
                .role(userRole)
                .enabled(true)
                .build();

        return userRepository.save(newUser);
    }

    @Override
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User blockUser(Long id) {
        User user = userRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(false);
        return userRepository.save(user);
    }

    @Override
    public User unlockUser(Long id) {
        User user = userRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(true);
        return userRepository.save(user);
    }

    @Transactional
    public void promoteToAdmin(String username) {
        User user = findByUsername(username);
        Role adminRole = roleRepository.findByRoleName("ADMIN")
                .orElseThrow(() -> new RoleNotFoundException("Роль ADMIN не найдена"));
        user.setRole(adminRole);
        userRepository.save(user);
    }

    @Transactional
    public void promoteCurrentUserToAdmin() {
        User user = getCurrentUser();
        Role adminRole = roleRepository.findByRoleName("ADMIN")
                .orElseThrow(() -> new RoleNotFoundException("Роль ADMIN не найдена"));
        user.setRole(adminRole);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(Math.toIntExact(id))) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(Math.toIntExact(id));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
