package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.encryption.AesEncryptionService;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cards_table")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number", length = 150, nullable = false)
    private String encryptedCardNumber;

    @Transient
    private String decryptedCardNumber;

    @ManyToOne
    @JoinColumn(name = "owner", nullable = false)
    private User owner;

    @Column(name = "validity_period", nullable = false)
    private LocalDate validityPeriod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CardStatus status;

    @Column(nullable = false)
    private BigDecimal balance;

    public String getCardNumber() {
        return decryptedCardNumber;
    }

    public void setCardNumber(String cardNumber, AesEncryptionService encryptionService) {
        this.decryptedCardNumber = cardNumber;
        this.encryptedCardNumber = encryptionService.encrypt(cardNumber);
    }

    public void decryptCardNumber(AesEncryptionService encryptionService) {
        if (this.encryptedCardNumber != null) {
            this.decryptedCardNumber = encryptionService.decrypt(this.encryptedCardNumber);
        }
    }
}
