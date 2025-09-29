package com.innowise.userservice.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

/**
 * @ClassName UpdateCardRequest
 * @Description DTO used for accepting card update input from API clients.
 * Encapsulates card ID, number, holder name, expiration date, and associated user ID.
 * Serves as a write-only projection for updating existing {@link com.innowise.userservice.database.entity.Card} entities.
 * @Author dshparko
 * @Date 08.09.2025 21:30
 * @Version 1.0
 */
public record CardDto(
        @NotNull(message = "Сard ID is required")
        Long id,

        @NotBlank(message = "Card number is required")
        @Pattern(regexp = "^\\d{13,19}$", message = "The number should be between 13 and 19 digits")
        String number,

        @NotBlank(message = "Holder name is required")
        String holder,

        @Future(message = "Expiration date must be in the future")
        LocalDate expirationDate,

        @NotNull(message = "User ID is required")
        Long userId) {
}
