package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 30, message = "Имя пользователя должно быть от 3 до 30 символов")
    private String username;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный email")
    private String email;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 6, max = 50, message = "Пароль должен быть от 6 до 50 символов")
    private String password;
}
