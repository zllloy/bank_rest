package com.example.bankcards.controller;

import com.example.bankcards.dto.BlockCardRequestUser;
import com.example.bankcards.dto.RequestsToBlockCardsDto;
import com.example.bankcards.service.impl.RequestsToBlockServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/requestsToBlock")
public class RequestToBlockController {
    private final RequestsToBlockServiceImpl requestsToBlockService;

    @GetMapping("/cards")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RequestsToBlockCardsDto>> getRequestsToBlockCards() {
        List<RequestsToBlockCardsDto> cards = requestsToBlockService.getRequestsToBlockCards();
        return ResponseEntity.ok(cards);
    }

    @PostMapping("/cards")
    public ResponseEntity<RequestsToBlockCardsDto> sendRequestsToBlockCards(
            @Valid @RequestBody BlockCardRequestUser request
    ) throws AccessDeniedException {
        requestsToBlockService.sendRequestsToBlockCards(request);
        return ResponseEntity.ok().build();
    }


}
