package com.antrip.auth_service.controllers;

import com.antrip.auth_service.configs.SecurityConfig;
import com.antrip.auth_service.exceptions.InvalidRegisterRequestException;
import com.antrip.auth_service.models.RegisterRequest;
import com.antrip.auth_service.models.UserRepository;
import com.antrip.auth_service.security.CustomUserDetailsService;
import com.antrip.auth_service.services.AuthService;
import com.antrip.auth_service.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, AuthService.class, JwtUtil.class, CustomUserDetailsService.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should register user and return JWT token")
    @SneakyThrows
    void register_Success() {
        RegisterRequest registerRequest = new RegisterRequest("Test User", "test@example.com", "password123");
        String expectedToken = "mocked-jwt-token";

        doNothing().when(authService).register(
                registerRequest.displayName(),
                registerRequest.email(),
                registerRequest.password()
        );

        when(authService.login(registerRequest.email(), registerRequest.password()))
                .thenReturn(expectedToken);

        mockMvc.perform(post("/auth/register")
                        .content(objectMapper.writeValueAsString(registerRequest))
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value(expectedToken));

        verify(authService, times(1)).register(
                registerRequest.displayName(),
                registerRequest.email(),
                registerRequest.password()
        );
        verify(authService, times(1)).login(
                registerRequest.email(),
                registerRequest.password()
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\n"})
    @DisplayName("Should throw InvalidRegisterRequestException if displayName is invalid")
    void register_invalidDisplayName(String invalidDisplayName) {
        InvalidRegisterRequestException ex = assertThrows(
                InvalidRegisterRequestException.class,
                () -> new RegisterRequest(invalidDisplayName, "test@example.com", "password123")
        );
        assertEquals("Display name cannot be null or blank", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\n"})
    @DisplayName("Should throw InvalidRegisterRequestException if email is invalid")
    void register_invalidEmail(String invalidEmail) {
        InvalidRegisterRequestException ex = assertThrows(
                InvalidRegisterRequestException.class,
                () -> new RegisterRequest("display name", invalidEmail, "password123")
        );
        assertEquals("Email cannot be null or blank", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\n"})
    @DisplayName("Should throw InvalidRegisterRequestException if password is invalid")
    void register_invalidPassword(String invalidPassword) {
        InvalidRegisterRequestException ex = assertThrows(
                InvalidRegisterRequestException.class,
                () -> new RegisterRequest("display name", "test@example.com", invalidPassword)
        );
        assertEquals("Password cannot be null or blank", ex.getMessage());
    }
}