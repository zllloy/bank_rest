package com.example.bankcards.entity.mappers;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.RequestBlockCardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.RequestToBlockCards;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CardsMapper {

    public CardDto toDto(Card card) {
        CardDto dto = new CardDto();
        dto.setOwnerId(card.getOwner().getId());
        dto.setStatus(card.getStatus());
        dto.setMaskedNumber(card.getEncryptedCardNumber());
        dto.setBalance(card.getBalance());
        dto.setValidityPeriod(card.getValidityPeriod());
        return dto;
    }

    public List<CardDto> toDto(List<Card> cards) {
        List<CardDto> dtos = new ArrayList<>();
        for (Card card : cards) {
            dtos.add(toDto(card));
        }
        return dtos;
    }

    public RequestBlockCardDto toDtoRequestBlockCard (RequestToBlockCards requestsToBlockCards) {
        RequestBlockCardDto dto = new RequestBlockCardDto();
        dto.setId(requestsToBlockCards.getId());
        dto.setBalance(requestsToBlockCards.getCard().getBalance());
        dto.setMaskedNumber(requestsToBlockCards.getCard().getEncryptedCardNumber());
        dto.setStatus(requestsToBlockCards.getCard().getStatus());

        return dto;
    }

    public List<RequestBlockCardDto> toDtoRequestBlockCard (List<RequestToBlockCards> requestsToBlockCards) {
        List<RequestBlockCardDto> dtos = new ArrayList<>();
        for (RequestToBlockCards requestToBlockCards : requestsToBlockCards) {
            dtos.add(toDtoRequestBlockCard(requestToBlockCards));
        }

        return dtos;
    }
}

