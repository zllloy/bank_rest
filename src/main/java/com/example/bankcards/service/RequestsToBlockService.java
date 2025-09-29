package com.example.bankcards.service;

import com.example.bankcards.dto.BlockCardRequestUser;
import com.example.bankcards.dto.RequestsToBlockCardsDto;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface RequestsToBlockService {

    List<RequestsToBlockCardsDto> getRequestsToBlockCards();

    void sendRequestsToBlockCards( BlockCardRequestUser id) throws AccessDeniedException;
}
