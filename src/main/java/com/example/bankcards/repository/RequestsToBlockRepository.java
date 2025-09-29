package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.RequestToBlockCards;
import com.example.bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestsToBlockRepository extends JpaRepository<RequestToBlockCards, Integer> {
    boolean existsByOwnerAndCard(User user, Card card);
}
