package com.example.bankcards.controller;

import com.example.bankcards.config.TestConfig;
import com.example.bankcards.dto.*;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.service.impl.ErrorServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardsController.class)
@Import(TestConfig.class)
public class CardsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ErrorServiceImpl errorServiceImpl;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateCardStatus_ShouldReturnNoContent_WhenValidRequest() throws Exception {
        Long cardId = 1L;
        CardStatusUpdateRequestDto requestDto = new CardStatusUpdateRequestDto();
        requestDto.setStatus("ACTIVE");

        doNothing().when(cardService).updateStatus(anyLong(), any());

        mockMvc.perform(patch("/api/v1/cards/{id}/status", cardId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void updateCardStatus_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        Long cardId = 1L;
        CardStatusUpdateRequestDto requestDto = new CardStatusUpdateRequestDto();
        requestDto.setStatus("ACTIVE");

        mockMvc.perform(patch("/api/v1/cards/{id}/status", cardId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void createCard_ShouldReturnOk_WhenValidRequest() throws Exception {
        CardCreateRequestDto requestDto = new CardCreateRequestDto();
        requestDto.setCardNumber("1234567812345678");
        requestDto.setOwnerId(1);
        requestDto.setExpirationDate(LocalDate.now().plusYears(2));

        mockMvc.perform(post("/api/v1/cards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void createCard_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        CardCreateRequestDto requestDto = new CardCreateRequestDto();
        requestDto.setCardNumber("1234567812345678");
        requestDto.setOwnerId(1);
        requestDto.setExpirationDate(LocalDate.now().plusYears(2));

        mockMvc.perform(post("/api/v1/cards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    // Новые тесты:

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAllCards_ShouldReturnCardsList_WhenAdmin() throws Exception {
        Long user1 = 1L;
        Long user2 = 2L;

        CardDto card1 = new CardDto();
        card1.setMaskedNumber("***********5678");
        card1.setBalance(BigDecimal.valueOf(100));
        card1.setValidityPeriod(LocalDate.now().plusYears(2));
        card1.setStatus(CardStatus.ACTIVE);
        card1.setOwnerId(user1);

        CardDto card2 = new CardDto();
        card2.setMaskedNumber("***********4321");
        card2.setBalance(BigDecimal.valueOf(100));
        card2.setValidityPeriod(LocalDate.now().plusYears(2));
        card2.setStatus(CardStatus.ACTIVE);
        card2.setOwnerId(user2);

        List<CardDto> cards = List.of(card1, card2);

        when(cardService.getAllCards()).thenReturn(cards);

        mockMvc.perform(get("/api/v1/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].maskedNumber").value("***********5678"))
                .andExpect(jsonPath("$[0].balance").value(100))
                .andExpect(jsonPath("$[0].ownerId").value(1))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[1].maskedNumber").value("***********4321"))
                .andExpect(jsonPath("$[1].ownerId").value(2))
                .andExpect(jsonPath("$[1].status").value("ACTIVE"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getAllCards_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/cards"))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteCard_ShouldReturnNoContent_WhenAdmin() throws Exception {
        doNothing().when(cardService).deleteCard(1L);

        mockMvc.perform(delete("/api/v1/cards/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(cardService, times(1)).deleteCard(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void deleteCard_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/cards/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getUserCards_ShouldReturnUserCards_WhenAdmin() throws Exception {
        Long user = 1L;

        CardDto card = new CardDto();
        card.setMaskedNumber("***********4321");
        card.setBalance(BigDecimal.valueOf(100));
        card.setValidityPeriod(LocalDate.now().plusYears(2));
        card.setStatus(CardStatus.ACTIVE);
        card.setOwnerId(user);

        Page<CardDto> page = new PageImpl<>(List.of(card));

        when(cardService.getUserCards(eq(1L), any(), eq(null), eq(null)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/cards/user/1/cards")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void getMyCards_ShouldReturnUserCards_WhenAuthenticatedUser() throws Exception {
        Long user = 1L;

        Role role = new Role();
        role.setId(2L);
        role.setRoleName("USER");

        User currentUser = new User();
        currentUser.setId(user);
        currentUser.setEnabled(true);
        currentUser.setRole(role);
        currentUser.setEmail("user@user.com");
        currentUser.setUsername("user1");
        currentUser.setPassword("123456");

        CardDto card = new CardDto();
        card.setMaskedNumber("***********4321");
        card.setBalance(BigDecimal.valueOf(100));
        card.setValidityPeriod(LocalDate.now().plusYears(2));
        card.setStatus(CardStatus.ACTIVE);
        card.setOwnerId(user);

        Page<CardDto> page = new PageImpl<>(List.of(card));

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(cardService.getUserCards(eq(1L), any(), eq(null), eq(null)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/cards/my-cards")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getBalance_ShouldReturnBalance_WhenValidRequest() throws Exception {
        GetBalanceCard request = new GetBalanceCard();
        request.setId(1L);

        BigDecimal balance = new BigDecimal("1000.5");

        when(cardService.getBalance(any(GetBalanceCard.class))).thenReturn(balance);

        mockMvc.perform(get("/api/v1/cards/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("1000.5"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void transfer_ShouldProcessTransfer_WhenValidRequest() throws Exception {
        TransferBetweenCardsDto transfer = new TransferBetweenCardsDto();
        transfer.setFromCardId(1L);
        transfer.setToCardId(2L);
        transfer.setAmount(new BigDecimal("100.00"));

        doNothing().when(cardService).transferBetweenCards(any(TransferBetweenCardsDto.class));

        mockMvc.perform(post("/api/v1/cards/transfer")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transfer)))
                .andExpect(status().isOk());

        verify(cardService, times(1)).transferBetweenCards(any(TransferBetweenCardsDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void createCard_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        CardCreateRequestDto invalidRequest = new CardCreateRequestDto();
        invalidRequest.setCardNumber("123");
        invalidRequest.setOwnerId(1);
        invalidRequest.setExpirationDate(LocalDate.now().plusYears(2));

        mockMvc.perform(post("/api/v1/cards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}