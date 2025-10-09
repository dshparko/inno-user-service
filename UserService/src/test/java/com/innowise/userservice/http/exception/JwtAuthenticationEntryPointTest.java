package com.innowise.userservice.http.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.innowise.userservice.dto.error.ErrorResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import static com.innowise.userservice.config.AuthConstant.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthenticationEntryPointTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    private final JwtAuthenticationEntryPoint entryPoint = new JwtAuthenticationEntryPoint(objectMapper);

    @Test
    void shouldReturnJsonErrorResponseWith401() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");

        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException exception = new AuthenticationException("Invalid token") {
        };

        // when
        entryPoint.commence(request, response, exception);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(response.getContentType()).isEqualTo(CONTENT_TYPE);

        ErrorResponseDto dto = objectMapper.readValue(response.getContentAsString(), ErrorResponseDto.class);
        assertThat(dto.status()).isEqualTo(401);
        assertThat(dto.error()).contains("Invalid token");
        assertThat(dto.path()).isEqualTo("/api/test");
    }
}
