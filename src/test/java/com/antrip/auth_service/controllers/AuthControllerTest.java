package com.antrip.auth_service.controllers;

import com.antrip.auth_service.configs.SecurityConfig;
import com.antrip.auth_service.models.RegisterRequest;
import com.antrip.auth_service.models.UserRepository;
import com.antrip.auth_service.security.CustomUserDetailsService;
import com.antrip.auth_service.services.AuthService;
import com.antrip.auth_service.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
    void register_Success() throws Exception {
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
}