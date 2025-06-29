package com.antrip.auth_service.utils;

import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String secret;
    private long expirationMs;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        secret = Base64.getEncoder().encodeToString(key.getEncoded());
        expirationMs = 1000 * 60 * 60;

        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", secret);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", expirationMs);
    }

    @Test
    @DisplayName("Should generate and extract username from token")
    void testGenerateAndExtractToken() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        assertNotNull(token);

        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("Should validate token with correct username")
    void testValidateToken_Valid() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        assertTrue(jwtUtil.validateToken(token, username));
    }

    @Test
    @DisplayName("Should not validate token with incorrect username")
    void testValidateToken_InvalidUsername() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        assertFalse(jwtUtil.validateToken(token, "otheruser"));
    }
}