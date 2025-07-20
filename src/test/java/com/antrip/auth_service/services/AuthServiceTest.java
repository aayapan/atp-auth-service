package com.antrip.auth_service.services;

import com.antrip.auth_service.exceptions.UserAlreadyExistsException;
import com.antrip.auth_service.models.User;
import com.antrip.auth_service.models.UserRepository;
import com.antrip.auth_service.utils.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    @DisplayName("Should register new user when email does not exist")
    void testRegister_Success() {
        String displayName = "Test User";
        String email = "test@example.com";
        String password = "password";
        String encodedPassword = "encodedPassword";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        authService.register(displayName, email, password);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals(displayName, savedUser.getDisplayName());
        assertEquals(email, savedUser.getEmail());
        assertEquals(encodedPassword, savedUser.getPassword());
        assertNotNull(savedUser.getDateCreated());
        assertNotNull(savedUser.getDateUpdated());
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void testRegister_UserAlreadyExists() {
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () ->
                authService.register("Test User", email, "password")
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return JWT token on successful login")
    void testLogin_ReturnsJwtToken() {
        String email = "user@example.com";
        String password = "password";
        String jwt = "jwt.token";

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(email, password);

        when(authenticationManager.authenticate(any())).thenReturn(authToken);
        when(jwtUtil.generateToken(email)).thenReturn(jwt);

        String result = authService.login(email, password);

        assertEquals(jwt, result);
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        verify(jwtUtil).generateToken(email);
    }

    @Test
    @DisplayName("Should throw exception when authentication fails")
    void testLogin_AuthenticationFails() {
        String email = "test@example.com";
        String password = "wrongpassword";

        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("Bad credentials"));

        assertThrows(RuntimeException.class, () -> authService.login(email, password));
        verify(jwtUtil, never()).generateToken(any());
    }
}
