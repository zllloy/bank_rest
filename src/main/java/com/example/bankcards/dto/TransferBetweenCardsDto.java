package com.example.bankcards.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
public class TransferBetweenCardsDto {
    @NotNull(message = "Укажите id исходной карты")
    @Min(value = 1, message = "Номер исходной карты не может быть отрицательным или нулевым")
    private Long fromCardId;

    @NotNull(message = "Укажите id целевой карты")
    @Min(value = 1, message = "Номер целевой карты не может быть отрицательным или нулевым")
    private Long toCardId;

    @NotNull(message = "Укажите сумму перевода")
    @DecimalMin(value = "0.01", message = "Сумма перевода должна быть больше нуля")
    private BigDecimal amount;

}
