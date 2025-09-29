package com.example.bankcards.validation;


import com.example.bankcards.entity.enums.CardStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class CardStatusValidator implements ConstraintValidator<ValidCardStatus, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return Arrays.stream(CardStatus.values())
                .anyMatch(status -> status.name().equalsIgnoreCase(value));
    }
}
