package com.innowise.userservice.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.security.Key;
import java.util.Date;

import static com.innowise.userservice.config.AuthConstant.AUTH_HEADER;
import static com.innowise.userservice.config.AuthConstant.TOKEN_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class JwtAuthenticationFilterTest {

    @Spy
    private JwtAuthenticationFilter filter;

    private final String rawSecret = "daryaSuperSecretKeyForJwtTesting1234567890";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();

        String encodedSecret = java.util.Base64.getEncoder().encodeToString(rawSecret.getBytes());
        ReflectionTestUtils.setField(filter, "secretKey", encodedSecret);
    }

    private String generateToken(String subject, String role) {
        Key key = Keys.hmacShaKeyFor(rawSecret.getBytes());

        return Jwts.builder()
                .setSubject(subject)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void shouldAuthenticateWithValidToken() throws ServletException, IOException {
        String token = generateToken("testuser", "ADMIN");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTH_HEADER, TOKEN_PREFIX + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo("testuser");
        assertThat(auth.getAuthorities()).extracting("authority").contains("ROLE_ADMIN");
    }

    @Test
    void shouldNotAuthenticateWithInvalidToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTH_HEADER, "Bearer invalid.token.value");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldSkipAuthenticationIfNoToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}