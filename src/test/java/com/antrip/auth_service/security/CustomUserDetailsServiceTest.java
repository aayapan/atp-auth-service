package com.antrip.auth_service.security;

import com.antrip.auth_service.models.User;
import com.antrip.auth_service.models.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @InjectMocks
    CustomUserDetailsService customUserDetailsService;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("Should return UserDetails when user is found by email")
    public void testLoadUserByUsername_success() {
        final String email = "email@email.com";
        final String password = "password";
        final Collection<GrantedAuthority> emptyAuthorities = Collections.emptyList();

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(), user.getPassword(), emptyAuthorities
                );

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userReturned = customUserDetailsService.loadUserByUsername(email);

        verify(userRepository).findByEmail(anyString());

        assertNotNull(userReturned);
        assertEquals(userDetails.getUsername(), userReturned.getUsername());
        assertEquals(userDetails.getPassword(), userReturned.getPassword());
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user is not found by email")
    public void testLoadUserByUsername_userNotFound() {
        final String nonExistentEmail = "invalid@email.com";

        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        try {
            customUserDetailsService.loadUserByUsername(nonExistentEmail);
        } catch (UsernameNotFoundException e) {
            assertEquals("User not found with email: " + nonExistentEmail, e.getMessage());
        }
    }

}
