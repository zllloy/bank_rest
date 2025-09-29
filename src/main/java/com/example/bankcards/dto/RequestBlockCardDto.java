package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.CardStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestBlockCardDto {
    private Long id;
    private String maskedNumber;
    private BigDecimal balance;
    private CardStatus status;
}
