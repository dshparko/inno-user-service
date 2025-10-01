package com.innowise.userservice.dto.error;

/**
 * @ClassName ValidationErrorDto
 * @Description DTO used for exposing field-level validation errors in API responses.
 * Encapsulates the name of the invalid field, the validation message, and the rejected value.
 * Serves as a structured component of {@link com.innowise.userservice.dto.error.ErrorResponseDto} for detailed feedback.
 * @Author dshparko
 * @Date 16.09.2025 15:34
 * @Version 1.0
 */
public record ValidationErrorDto(
        String field,
        String message,
        String rejectedValue
) {
}
