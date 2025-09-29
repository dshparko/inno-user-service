package com.innowise.userservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * @ClassName UserWithCardsResponse
 * @Description * DTO representing a user along with their associated cards.
 * Includes basic personal information and a list of {@link CardDto} objects
 * for downstream consumption in API responses or service layers.
 * @Author dshparko
 * @Date 16.09.2025 10:57
 * @Version 1.0
 */
public record UserDto(
        @NotNull(message = "User ID is required")
        Long id,

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Surname is required")
        String surname,

        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        String email,

        @Past(message = "Birth date must be in the past")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate birthDate,

        List<CardDto> cards
) implements Serializable {
}
