package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateRequestDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.GetBalanceCard;
import com.example.bankcards.dto.TransferBetweenCardsDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;

public interface CardService {

    void createCard(CardCreateRequestDto request) throws AccessDeniedException;

    Page<CardDto> getUserCards(Long userId, Pageable pageable, String search, CardStatus status);

    Card getCardByNumber(String cardNumber);

    @Transactional
    void updateStatus(Long cardId, CardStatus status);

    void deleteCard(Long id);

    List<CardDto> getAllCards();

    BigDecimal getBalance(GetBalanceCard id) throws AccessDeniedException;

    void transferBetweenCards(TransferBetweenCardsDto transfer) throws AccessDeniedException;
}
