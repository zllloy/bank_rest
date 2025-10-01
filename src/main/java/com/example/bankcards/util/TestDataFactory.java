package com.example.bankcards.util;

import com.example.bankcards.dto.CardCreateRequestDto;
import com.example.bankcards.dto.GetBalanceCard;
import com.example.bankcards.dto.TransferBetweenCardsDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.encryption.AesEncryptionService;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
public class TestDataFactory {

    public static User createUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(username + "@test.com");
        return user;
    }

    public static Card createCard(Long id, String cardNumber, User owner, BigDecimal balance, AesEncryptionService encryptionService) {

        Card card = new Card();
        card.setId(id);
        card.setCardNumber(cardNumber, encryptionService);
        card.setOwner(owner);
        card.setBalance(balance);
        card.setStatus(CardStatus.ACTIVE);
        card.setValidityPeriod(LocalDate.now().plusYears(2));
        return card;
    }

    public static CardCreateRequestDto createCardRequestDto(String cardNumber, Integer ownerId) {
        CardCreateRequestDto dto = new CardCreateRequestDto();
        dto.setCardNumber(cardNumber);
        dto.setOwnerId(ownerId);
        dto.setExpirationDate(LocalDate.now().plusYears(2));
        return dto;
    }

    public static GetBalanceCard createGetBalanceCard(Long cardId) {
        GetBalanceCard dto = new GetBalanceCard();
        dto.setId(cardId);
        return dto;
    }

    public static TransferBetweenCardsDto createTransferDto(Long fromCardId, Long toCardId, BigDecimal amount) {
        TransferBetweenCardsDto dto = new TransferBetweenCardsDto();
        dto.setFromCardId(fromCardId);
        dto.setToCardId(toCardId);
        dto.setAmount(amount);
        return dto;
    }
}