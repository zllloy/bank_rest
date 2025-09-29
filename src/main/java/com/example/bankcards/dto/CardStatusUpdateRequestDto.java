package com.example.bankcards.dto;

import com.example.bankcards.validation.ValidCardStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CardStatusUpdateRequestDto {

    @NotNull(message = "Status is required")
    @ValidCardStatus
    private String status;

}
