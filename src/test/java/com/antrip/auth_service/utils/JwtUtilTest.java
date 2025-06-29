package com.antrip.auth_service.utils;

import io.jsonwebtoken.Jwts;
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

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        SecretKey key = Jwts.SIG.HS256.key().build();
        String secret = Base64.getEncoder().encodeToString(key.getEncoded());
        long expirationMs = 1000 * 60 * 60;

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

    @Test
    @DisplayName("Should not validate expired token")
    void testValidateToken_Expired() throws InterruptedException {
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 1L);
        String token = jwtUtil.generateToken("testuser");
        Thread.sleep(5);

        assertFalse(jwtUtil.validateToken(token, "testuser"));
    }
}