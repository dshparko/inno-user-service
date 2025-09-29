package com.innowise.userservice.http.exception;

import com.innowise.userservice.dto.error.ErrorResponseDto;
import com.innowise.userservice.dto.error.ValidationErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.UUID;

/**
 * @ClassName ApiErrorHandler
 * @Description Global exception handler for REST API.
 * Supports validation errors, resource not found exceptions, malformed requests, and generic server errors.
 * @Author dshparko
 * @Date 11.09.2025 21:05
 * @Version 1.0
 */
@RestControllerAdvice
public class ApiErrorHandler {

    /**
     * Handles validation errors triggered by {@code @Valid} annotated request bodies.
     * Converts field-level errors into a list of {@link ValidationErrorDto} for client-side feedback.
     *
     * @param ex the validation exception containing binding results
     * @return HTTP 400 Bad Request with detailed validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationErrorDto>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<ValidationErrorDto> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationErrorDto(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue() != null ? error.getRejectedValue().toString() : null
                ))
                .toList();

        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handles domain-specific not found exceptions such as {@link ResourceNotFoundException} .
     * Constructs a standardized {@link ErrorResponseDto} with HTTP 404 status.
     *
     * @param ex      the thrown exception
     * @param request the originating HTTP request
     * @return HTTP 404 Not Found with error details
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(ResourceNotFoundException ex,
                                                           HttpServletRequest request) {
        ErrorResponseDto response = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getRequestURI(),
                ex.getErrorId()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handles malformed JSON or unreadable request bodies.
     *
     * @param ex      the exception indicating unreadable input
     * @param request the originating HTTP request
     * @return HTTP 400 Bad Request with error message
     */
    @ExceptionHandler(HttpMessageConversionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDto> handleBadRequest(HttpMessageConversionException ex,
                                                             HttpServletRequest request) {
        ErrorResponseDto response = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI(),
                UUID.randomUUID()
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ModificationException.class)
    public ResponseEntity<ErrorResponseDto> handleCardUpdateFailed(ModificationException ex,
                                                                   HttpServletRequest request) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                request.getRequestURI(),
                ex.getErrorId()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handles all uncaught exceptions not explicitly mapped.
     *
     * @param ex      the thrown exception
     * @param request the originating HTTP request
     * @return HTTP 500 Internal Server Error with error details
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponseDto> handleOther(Exception ex,
                                                        HttpServletRequest request) {
        ErrorResponseDto response = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                request.getRequestURI(),
                UUID.randomUUID()
        );

        return ResponseEntity.internalServerError().body(response);
    }
}
