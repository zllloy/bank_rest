package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByEncryptedCardNumber(String encryptedNumber);

    boolean existsByEncryptedCardNumber(String encryptedNumber);

    Page<Card> findByOwner_Id(Long ownerId, Pageable pageable);

    Page<Card> findByOwner_IdAndStatus(Long ownerId, CardStatus status, Pageable pageable);

    Page<Card> findByOwner_IdAndCardNumberContaining(Long ownerId, String number, Pageable pageable);

}

