package com.example.bankcards.service.impl;

import com.example.bankcards.dto.JwtAuthenticationResponseDto;
import com.example.bankcards.dto.SignInRequestDto;
import com.example.bankcards.dto.SignUpRequestDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.RoleNotFoundException;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;

    public JwtAuthenticationResponseDto signUp(SignUpRequestDto request) throws RoleNotFoundException {

        User user = userService.createUser(
                new UserDto(
                        request.getUsername(),
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponseDto(jwt);
    }

    public JwtAuthenticationResponseDto signIn(SignInRequestDto request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        User user = userService.findByUsername(request.getUsername());

        String jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponseDto(jwt);
    }
}
