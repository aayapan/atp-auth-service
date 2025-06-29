package com.antrip.auth_service.utils;

import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Base64;

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
}