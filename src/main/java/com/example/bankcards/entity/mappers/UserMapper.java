package com.example.bankcards.entity.mappers;

import com.example.bankcards.dto.RequestBlockUserDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserResponseDto;
import com.example.bankcards.entity.RequestToBlockCards;
import com.example.bankcards.entity.User;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {
    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());

        return dto;
    }

    public List<UserDto> toDto(List<User> cards) {
        List<UserDto> dtos = new ArrayList<>();
        for (User card : cards) {
            dtos.add(toDto(card));
        }
        return dtos;
    }

    public RequestBlockUserDto toDtoRequestBlockUser(RequestToBlockCards user) {
        RequestBlockUserDto dto = new RequestBlockUserDto();
        dto.setUsername(user.getOwner().getUsername());

        return dto;
    }

    public User backToUser (UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        return user;
    }

    public UserResponseDto userResponseToDto (UserDto userDto) {
        UserResponseDto userResponseDto = new UserResponseDto();

        userResponseDto.setEmail(userDto.getEmail());
        userResponseDto.setUsername(userDto.getUsername());

        return userResponseDto;
    }
}
