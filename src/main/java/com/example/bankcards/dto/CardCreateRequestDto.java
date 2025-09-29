package com.example.bankcards.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class CardCreateRequestDto {
    @NotBlank(message = "Номер карты обязателен")
    @Pattern(regexp = "\\d{16}", message = "Номер карты должен содержать 16 цифр")
    private String cardNumber;

    @NotNull(message = "Владелец карты обязателен")
    @Min(value = 1, message = "Id пользователя не может быть меньше 1")
    private Integer ownerId;

    @NotNull(message = "Дата истечения обязательна")
    @Future(message = "Дата истечения должна быть в будущем")
    private LocalDate expirationDate;
}

