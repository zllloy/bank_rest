package com.example.bankcards.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class BlockCardRequestUser {
    @NotNull(message = "Id карты обязательно должен быть указан.")
    @Min(value = 1, message = "Id карты не может быть меньше 1.")
    private Long id;
}
