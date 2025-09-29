package com.example.bankcards.service.impl;

import com.example.bankcards.dto.BlockCardRequestUser;
import com.example.bankcards.dto.RequestsToBlockCardsDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.RequestToBlockCards;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.mappers.RequestToBlockCardsMapper;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.RequestsToBlockRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.RequestsToBlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestsToBlockServiceImpl implements RequestsToBlockService {
    private final RequestsToBlockRepository requestsToBlockRepository;
    private final RequestToBlockCardsMapper requestToBlockCardsMapper;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;

    @Override
    public List<RequestsToBlockCardsDto> getRequestsToBlockCards() {
        List<RequestToBlockCards> requestToBlockCards = requestsToBlockRepository.findAll();
        return requestToBlockCardsMapper.toDto(requestToBlockCards);
    }

    @Override
    public void sendRequestsToBlockCards(BlockCardRequestUser id) throws AccessDeniedException {
        if (id == null) {
            throw new IllegalArgumentException("ID карты не может быть пустым");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("Пользователь не авторизован");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        if (!cardRepository.existsById(id.getId())) {
            throw new CardNotFoundException("Карточка с таким id не найдена");
        }

        Card card = cardRepository.findById(id.getId())
                .orElseThrow(() -> new CardNotFoundException("Карточка с таким id не найдена"));

        if (card.getOwner().getId() != user.getId()) {
            throw new AccessDeniedException("Вы не можете отправить запрос блокировки чужой карты!");
        }

        if (requestsToBlockRepository.existsByOwnerAndCard(user, card)) {
            throw new IllegalStateException("Запрос на блокировку этой карты уже существует");
        }

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new IllegalStateException("Карта уже заблокирована");
        }

        RequestToBlockCards requestsToBlock = new RequestToBlockCards();
        requestsToBlock.setCard(card);
        requestsToBlock.setOwner(user);

        requestsToBlockRepository.save(requestsToBlock);
    }
}
