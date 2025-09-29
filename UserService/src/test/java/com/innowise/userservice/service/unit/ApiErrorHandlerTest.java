package com.innowise.userservice.service.unit;

import com.innowise.userservice.dto.error.ErrorResponseDto;
import com.innowise.userservice.dto.error.ValidationErrorDto;
import com.innowise.userservice.http.exception.ApiErrorHandler;
import com.innowise.userservice.http.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiErrorHandlerTest {

    private final ApiErrorHandler handler = new ApiErrorHandler();

    @Test
    @DisplayName("Validation errors")
    void shouldThrowValidationError() {
        FieldError fieldError = new FieldError(
                "objectName",
                "email",
                "invalid@example.com",
                false,
                null,
                null,
                "Email must be valid"
        );

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<List<ValidationErrorDto>> response = new ApiErrorHandler().handleValidationErrors(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertEquals("invalid@example.com", response.getBody().getFirst().rejectedValue());
    }

    @Test
    @DisplayName("404 not found")
    void shouldReturn404WithErrorResponse() {

        ResourceNotFoundException ex = new ResourceNotFoundException("User", 42);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/users/42");


        ResponseEntity<ErrorResponseDto> response = handler.handleNotFound(ex, request);


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().error()).isEqualTo("User with identifier 42 wasn't found");
        assertThat(response.getBody().path()).isEqualTo("/users/42");
    }

    @Test
    @DisplayName("400 bad request")
    void shouldReturn400WithErrorResponse() {
        HttpMessageConversionException ex = new HttpMessageConversionException("Malformed JSON");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/users");

        ResponseEntity<ErrorResponseDto> response = handler.handleBadRequest(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().error()).contains("Malformed JSON");
        assertThat(response.getBody().path()).isEqualTo("/users");
    }

    @Test
    @DisplayName("500 internal server error")
    void shouldReturn500WithErrorResponse() {

        Exception ex = new RuntimeException("Internal server error");
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/users");


        ResponseEntity<ErrorResponseDto> response = handler.handleOther(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().error()).isEqualTo("Internal server error");
        assertThat(response.getBody().path()).isEqualTo("/users");
    }
}
