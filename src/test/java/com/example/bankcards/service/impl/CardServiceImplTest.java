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
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.encryption.AesEncryptionService;
import com.example.bankcards.util.CardNumberMasker;
import com.example.bankcards.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Card Service Unit Tests")
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AesEncryptionService encryptionService;

    @Mock
    private CardNumberMasker masker;

    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardsMapper cardsMapper;

    private User testUser;
    private Card testCard;

    @BeforeEach
    void setUp() {
        testUser = TestDataFactory.createUser(1L, "testuser");
        testCard = TestDataFactory.createCard(1L, "1234567812345678", testUser, new BigDecimal("1000.00"), encryptionService);
    }

    @Test
    @DisplayName("Should update card status when card exists")
    void updateCardStatus_WhenCardExists_ShouldUpdateStatus() {
        Long cardId = 1L;
        CardStatus newStatus = CardStatus.ACTIVE;

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cardService.updateStatus(cardId, newStatus);

        assertEquals(newStatus, testCard.getStatus(), "Статус карты должен измениться на ACTIVE");
        verify(cardRepository).findById(cardId);
        verify(cardRepository).save(testCard);
    }

    @Test
    @DisplayName("Should throw exception when card not found")
    void updateCardStatus_WhenCardNotFound_ShouldThrowException() {
        Long cardId = 999L;
        CardStatus newStatus = CardStatus.ACTIVE;

        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        CardNotFoundException exception = assertThrows(
                CardNotFoundException.class,
                () -> cardService.updateStatus(cardId, newStatus)
        );

        assertEquals("Card not found", exception.getMessage());
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    public void getAllCards_ShouldReturnAllCards() {
        Card card1 = new Card();
        Card card2 = new Card();
        List<Card> cards = List.of(card1, card2);

        CardDto cardDto1 = new CardDto();
        CardDto cardDto2 = new CardDto();
        List<CardDto> expectedDtos = List.of(cardDto1, cardDto2);

        when(cardRepository.findAll()).thenReturn(cards);
        when(cardsMapper.toDto(cards)).thenReturn(expectedDtos);

        List<CardDto> result = cardService.getAllCards();

        assertEquals(expectedDtos, result);
        verify(cardRepository).findAll();
        verify(cardsMapper).toDto(cards);
    }

    @Test
    public void createCard_ShouldCreateCard_WhenAdminAndValidRequest() throws AccessDeniedException {
        CardCreateRequestDto request = new CardCreateRequestDto();
        request.setOwnerId(1);
        request.setCardNumber("1234567812345678");
        request.setExpirationDate(LocalDate.now().plusYears(2));

        User owner = new User();
        owner.setId(1L);

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getAuthorities()).thenAnswer(invocation -> {
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            return authorities;
        });
        when(userRepository.existsById(1)).thenReturn(true);
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(encryptionService.encrypt(anyString())).thenReturn("encrypted");

        cardService.createCard(request);

        verify(cardRepository).save(any(Card.class));
        verify(userRepository).existsById(1);
        verify(userRepository).findById(1);
    }

    @Test
    public void updateStatus_ShouldUpdateCardStatus_WhenCardExists() {
        // Given
        Long cardId = 1L;
        CardStatus newStatus = CardStatus.BLOCKED;
        Card card = new Card();
        card.setId(cardId);
        card.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        // When
        cardService.updateStatus(cardId, newStatus);

        // Then
        assertEquals(newStatus, card.getStatus());
        verify(cardRepository).findById(cardId);
        verify(cardRepository).save(card);
    }

    @Test
    public void updateStatus_ShouldThrowCardNotFoundException_WhenCardNotExists() {
        // Given
        Long cardId = 999L;
        CardStatus newStatus = CardStatus.ACTIVE;

        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CardNotFoundException.class, () -> cardService.updateStatus(cardId, newStatus));
        verify(cardRepository).findById(cardId);
        verify(cardRepository, never()).save(any());
    }

    @Test
    public void deleteCard_ShouldDeleteCard_WhenCardExists() {
        // Given
        Long cardId = 1L;
        Card card = new Card();
        card.setId(cardId);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        // When
        cardService.deleteCard(cardId);

        // Then
        verify(cardRepository).findById(cardId);
        verify(cardRepository).delete(card);
    }

    @Test
    public void deleteCard_ShouldThrowCardNotFoundException_WhenCardNotExists() {
        // Given
        Long cardId = 999L;

        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CardNotFoundException.class, () -> cardService.deleteCard(cardId));
        verify(cardRepository).findById(cardId);
        verify(cardRepository, never()).delete(any());
    }

    @Test
    public void getBalance_ShouldReturnBalance_WhenUserOwnsCard() throws AccessDeniedException {
        Long cardId = 1L;
        GetBalanceCard request = new GetBalanceCard();
        request.setId(cardId);

        BigDecimal expectedBalance = new BigDecimal("1000.50");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Card card = new Card();
        card.setId(cardId);
        card.setOwner(user);
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(expectedBalance);

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(auth.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        BigDecimal result = cardService.getBalance(request);

        assertEquals(expectedBalance, result);
        verify(cardRepository).findById(cardId);
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    public void getBalance_ShouldThrowAccessDenied_WhenUserNotOwner() {
        Long cardId = 1L;
        GetBalanceCard request = new GetBalanceCard();
        request.setId(cardId);

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("currentuser");

        User cardOwner = new User();
        cardOwner.setId(2L);

        Card card = new Card();
        card.setId(cardId);
        card.setOwner(cardOwner);
        card.setStatus(CardStatus.ACTIVE);

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(auth.getName()).thenReturn("currentuser");
        when(userRepository.findByUsername("currentuser")).thenReturn(Optional.of(currentUser));
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        assertThrows(AccessDeniedException.class, () -> cardService.getBalance(request));
    }

    @Test
    public void getBalance_ShouldThrowIllegalState_WhenCardBlocked() {
        Long cardId = 1L;
        GetBalanceCard request = new GetBalanceCard();
        request.setId(cardId);

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Card card = new Card();
        card.setId(cardId);
        card.setOwner(user);
        card.setStatus(CardStatus.BLOCKED);

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(auth.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        assertThrows(IllegalStateException.class, () -> cardService.getBalance(request));
    }

    @Test
    public void transferBetweenCards_ShouldTransferSuccessfully_WhenValidConditions() throws AccessDeniedException {
        TransferBetweenCardsDto transfer = new TransferBetweenCardsDto();
        transfer.setFromCardId(1L);
        transfer.setToCardId(2L);
        transfer.setAmount(new BigDecimal("100.00"));

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setOwner(user);
        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setBalance(new BigDecimal("500.00"));

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setOwner(user);
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setBalance(new BigDecimal("200.00"));

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(auth.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        cardService.transferBetweenCards(transfer);

        assertEquals(new BigDecimal("400.00"), fromCard.getBalance());
        assertEquals(new BigDecimal("300.00"), toCard.getBalance());
        verify(cardRepository).save(fromCard);
        verify(cardRepository).save(toCard);
    }

    @Test
    public void transferBetweenCards_ShouldThrowIllegalState_WhenInsufficientFunds() {

        TransferBetweenCardsDto transfer = new TransferBetweenCardsDto();
        transfer.setFromCardId(1L);
        transfer.setToCardId(2L);
        transfer.setAmount(new BigDecimal("1000.00"));

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setOwner(user);
        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setBalance(new BigDecimal("500.00"));

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setOwner(user);
        toCard.setStatus(CardStatus.ACTIVE);

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(auth.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        assertThrows(IllegalStateException.class, () -> cardService.transferBetweenCards(transfer));
        verify(cardRepository, never()).save(any());
    }

}