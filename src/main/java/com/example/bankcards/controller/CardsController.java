package com.example.bankcards.controller;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cards")
public class CardsController {
    private final CardService cardsService;
    private final UserService usersService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<CardDto> getAllCards() {
        return cardsService.getAllCards();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDto> createCard(@Valid @RequestBody CardCreateRequestDto request) throws AccessDeniedException {
        cardsService.createCard(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateCardStatus(@PathVariable Long id, @RequestBody @Valid CardStatusUpdateRequestDto request) {
        cardsService.updateStatus(id, CardStatus.valueOf(request.getStatus().toUpperCase()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardsService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/cards")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #userId == principal.id)")
    public ResponseEntity<Page<CardDto>> getUserCards(@PathVariable Long userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search, @RequestParam(required = false) CardStatus status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CardDto> cards = cardsService.getUserCards(userId, pageable, search, status);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/my-cards")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<CardDto>> getMyCards(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search, @RequestParam(required = false) CardStatus status) {
        Long currentUserId = usersService.getCurrentUser().getId();
        return getUserCards(currentUserId, page, size, search, status);
    }


    @GetMapping("/balance")
    public BigDecimal getBalance(@Valid @RequestBody GetBalanceCard id) throws AccessDeniedException {
        return cardsService.getBalance(id);
    }

    @PostMapping("/transfer")
    public void transfer(@Valid @RequestBody TransferBetweenCardsDto transfer) throws AccessDeniedException {
        cardsService.transferBetweenCards(transfer);
    }

}
