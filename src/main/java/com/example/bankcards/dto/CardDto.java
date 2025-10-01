package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.util.CardNumberMasker;
import com.example.bankcards.validation.ValidCardStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {

    @NotBlank(message = "Номер карты не может быть пустым")
    @Size(min = 16, max = 16, message = "Номер карты должен содержать 16 символов")
    private String maskedNumber;

    @NotNull(message= "Вы должны обязательно указать владельца карты.")
    @Min(value = 1, message = "Номер владельца не может быть отрицательным или нулевым")
    private Long ownerId;

    @NotNull(message = "Срок годности карты важно указывать!")
    private LocalDate validityPeriod;

    @NotNull(message = "Status is required")
    @ValidCardStatus
    private CardStatus status;

    private BigDecimal balance;

    public static CardDto fromEntity(Card card, CardNumberMasker masker) {
        CardDto dto = new CardDto();

        String decryptedNumber = card.getCardNumber();
        dto.setMaskedNumber(masker.maskCardNumber(decryptedNumber));

        dto.setOwnerId(card.getOwner().getId());
        dto.setValidityPeriod(card.getValidityPeriod());
        dto.setStatus(card.getStatus());
        dto.setBalance(card.getBalance());

        return dto;
    }
}
