package com.example.bankcards.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CardStatusValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCardStatus {
    String message() default "Status must be one of: ACTIVE, BLOCKED, EXPIRED";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
