package com.example.bankcards.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestsToBlockCardsDto {
    private RequestBlockUserDto user;
    private RequestBlockCardDto card;
}
