package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardCreateRequestDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.GetBalanceCard;
import com.example.bankcards.dto.TransferBetweenCardsDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.mappers.CardsMapper;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.encryption.AesEncryptionService;
import com.example.bankcards.util.CardNumberMasker;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final AesEncryptionService encryptionService;
    private final UserRepository userRepository;
    private final CardNumberMasker masker;
    private final CardsMapper cardsMapper;

    @Override
    public List<CardDto> getAllCards() {
        List<Card> cards = cardRepository.findAll();
        return cardsMapper.toDto(cards);
    }

    @Override
    public void createCard(CardCreateRequestDto request) throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }

        if (!userRepository.existsById(request.getOwnerId())) {
            throw new UserNotFoundException("Пользователя с таким ID не найдено");
        }

        if (request.getOwnerId() == null) {
            throw new IllegalArgumentException("Вы должны указать id держателя карты.");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            log.warn("User {} attempted to create card without ADMIN role", authentication.getName());
            throw new AccessDeniedException("Only ADMIN users can create cards");
        }

        log.info("Admin {} is creating a new card", authentication.getName());

        User owner = userRepository.findById(request.getOwnerId()).get();

        Card card = new Card();
        card.setCardNumber(request.getCardNumber(), encryptionService);
        card.setOwner(owner);
        card.setValidityPeriod(request.getExpirationDate());
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.ZERO);

        cardRepository.save(card);
    }

    @Transactional
    @Override
    public void updateStatus(Long cardId, CardStatus status) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found"));
        card.setStatus(status);
        cardRepository.save(card);
    }

    public void deleteCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id: " + cardId));
        cardRepository.delete(card);
    }

    @Override
    public Page<CardDto> getUserCards(Long userId, Pageable pageable, String search, CardStatus status) {
        Page<Card> cards;

        if (StringUtils.isNotBlank(search)) {
            cards = cardRepository.findByOwner_IdAndCardNumberContaining(userId, search, pageable);
        } else if (status != null) {
            cards = cardRepository.findByOwner_IdAndStatus(userId, status, pageable);
        } else {
            cards = cardRepository.findByOwner_Id(userId, pageable);
        }

        cards.forEach(card -> card.decryptCardNumber(encryptionService));

        return cards.map(card -> CardDto.fromEntity(card, masker));
    }

    @Override
    public Card getCardByNumber(String cardNumber) {
        String encryptedNumber = encryptionService.encrypt(cardNumber);
        return cardRepository.findByEncryptedCardNumber(encryptedNumber)
                .map(card -> {
                    card.decryptCardNumber(encryptionService);
                    return card;
                })
                .orElseThrow(() -> new CardNotFoundException("Card not found"));
    }

    @Override
    public BigDecimal getBalance(GetBalanceCard id) throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("Пользователь не авторизован");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        Card card = cardRepository.findById(id.getId())
                .orElseThrow(() -> new CardNotFoundException("Карточка с таким id не найдена"));

        if (!card.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Вы не можете посмотреть баланс чужой карты!");
        }

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new IllegalStateException("Данная карта заблокирована");
        }

        return card.getBalance();
    }

    @Override
    @Transactional
    public void transferBetweenCards(TransferBetweenCardsDto transfer) throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("Пользователь не авторизован");
        }
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        Card fromCard = cardRepository.findById(transfer.getFromCardId())
                .orElseThrow(() -> new CardNotFoundException("Исходная карта не найдена"));

        Card toCard = cardRepository.findById(transfer.getToCardId())
                .orElseThrow(() -> new CardNotFoundException("Целевая карта не найдена"));

        if (fromCard.getId().equals(toCard.getId())) {
            throw new AccessDeniedException("Вы не можете сделать перевод на ту же карту, с которой его делаете");
        }

        if (!fromCard.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Вы не можете перевести деньги с чужой карты");
        }

        if(!toCard.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Вы не можете делать переводы на чужие карты");
        }

        if (fromCard.getStatus() == CardStatus.BLOCKED || toCard.getStatus() == CardStatus.BLOCKED) {
            throw new IllegalStateException("Одна из карт заблокирована");
        }

        if (fromCard.getBalance().compareTo(transfer.getAmount()) < 0) {
            throw new IllegalStateException("Недостаточно средств на карте");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(transfer.getAmount()));
        toCard.setBalance(toCard.getBalance().add(transfer.getAmount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }


}
