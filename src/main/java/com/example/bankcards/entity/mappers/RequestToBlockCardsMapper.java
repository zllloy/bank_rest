package com.example.bankcards.entity.mappers;

import com.example.bankcards.dto.RequestsToBlockCardsDto;
import com.example.bankcards.entity.RequestToBlockCards;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class RequestToBlockCardsMapper {
    private final CardsMapper cardsMapper;
    private final UserMapper userMapper;

    public RequestsToBlockCardsDto toDto(RequestToBlockCards requestsToBlockCards) {
        RequestsToBlockCardsDto dto = new RequestsToBlockCardsDto();
        dto.setUser(userMapper.toDtoRequestBlockUser(requestsToBlockCards));
        dto.setCard(cardsMapper.toDtoRequestBlockCard(requestsToBlockCards));
        return dto;
    }

    public List<RequestsToBlockCardsDto> toDto(List<RequestToBlockCards> requestsToBlockCards) {
        List<RequestsToBlockCardsDto> dtos = new ArrayList<>();
        for (RequestToBlockCards request : requestsToBlockCards) {
            dtos.add(toDto(request));
        }
        return dtos;
    }
}
