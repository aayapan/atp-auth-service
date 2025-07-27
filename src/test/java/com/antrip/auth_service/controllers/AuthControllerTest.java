package com.antrip.auth_service.controllers;

import com.antrip.auth_service.configs.SecurityConfig;
import com.antrip.auth_service.exceptions.InvalidAuthRequestException;
import com.antrip.auth_service.exceptions.UserAlreadyExistsException;
import com.antrip.auth_service.models.ErrorResponse;
import com.antrip.auth_service.models.LoginRequest;
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
import org.springframework.security.authentication.BadCredentialsException;
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

    @Test
    @DisplayName("Should throw UserAlreadyExistsException if user already exists")
    @SneakyThrows
    void register_userAlreadyExists() {
        RegisterRequest registerRequest = new RegisterRequest("Test User", "test@example.com", "password123");
        ErrorResponse expectedError = new ErrorResponse("User with email " + registerRequest.email() + " already exists", null);

        doThrow(new UserAlreadyExistsException(expectedError.message()))
                .when(authService).register(registerRequest.displayName(), registerRequest.email(), registerRequest.password());

        mockMvc.perform(post("/auth/register")
                        .content(objectMapper.writeValueAsString(registerRequest))
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(expectedError.message()))
                .andExpect(jsonPath("$.field").isEmpty());

        verify(authService, times(1)).register(
                registerRequest.displayName(),
                registerRequest.email(),
                registerRequest.password()
        );
        verify(authService, never()).login(registerRequest.email(), registerRequest.password());
    }

    @Test
    @DisplayName("Should login user and return JWT token")
    @SneakyThrows
    public void login_Success() {
        LoginRequest loginRequest = new LoginRequest("email@email.com", "password123");
        String expectedToken = "mocked-jwt-token";

        when(authService.login(loginRequest.email(), loginRequest.password()))
                .thenReturn(expectedToken);

        mockMvc.perform(post("/auth/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(expectedToken));

        verify(authService, times(1)).login(
                loginRequest.email(),
                loginRequest.password()
        );
    }

    @Test
    @DisplayName("Should throw BadCredentialsException if login request has doesn't match any user")
    @SneakyThrows
    void login_BadCredentials() {
        LoginRequest loginRequest = new LoginRequest("email@email.com", "password123");
        ErrorResponse expectedError = new ErrorResponse("Invalid email or password", null);

        doThrow(new BadCredentialsException(expectedError.message())).when(authService)
                .login(loginRequest.email(), loginRequest.password());

        mockMvc.perform(post("/auth/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(expectedError.message()))
                .andExpect(jsonPath("$.field").isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\n"})
    @DisplayName("Should throw InvalidAuthRequestException if displayName is invalid upon registration")
    void register_invalidDisplayName(String invalidDisplayName) {
        InvalidAuthRequestException ex = assertThrows(
                InvalidAuthRequestException.class,
                () -> new RegisterRequest(invalidDisplayName, "test@example.com", "password123")
        );
        assertEquals("Display name cannot be null or blank", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\n"})
    @DisplayName("Should throw InvalidAuthRequestException if email is invalid upon registration")
    void register_invalidEmail(String invalidEmail) {
        InvalidAuthRequestException ex = assertThrows(
                InvalidAuthRequestException.class,
                () -> new RegisterRequest("display name", invalidEmail, "password123")
        );
        assertEquals("Email cannot be null or blank", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\n"})
    @DisplayName("Should throw InvalidAuthRequestException if password is invalid upon registration")
    void register_invalidPassword(String invalidPassword) {
        InvalidAuthRequestException ex = assertThrows(
                InvalidAuthRequestException.class,
                () -> new RegisterRequest("display name", "test@example.com", invalidPassword)
        );
        assertEquals("Password cannot be null or blank", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\n"})
    @DisplayName("Should throw InvalidAuthRequestException if email is invalid upon login")
    void login_invalidEmail(String invalidEmail) {
        InvalidAuthRequestException ex = assertThrows(
                InvalidAuthRequestException.class,
                () -> new LoginRequest(invalidEmail, "password123")
        );
        assertEquals("Email cannot be null or blank", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\n"})
    @DisplayName("Should throw InvalidAuthRequestException if password is invalid upon login")
    void login_invalidPassword(String invalidPassword) {
        InvalidAuthRequestException ex = assertThrows(
                InvalidAuthRequestException.class,
                () -> new LoginRequest("test@example.com", invalidPassword)
        );
        assertEquals("Password cannot be null or blank", ex.getMessage());
    }
}