package com.antrip.auth_service.services;

import com.antrip.auth_service.exceptions.UserAlreadyExistsException;
import com.antrip.auth_service.models.User;
import com.antrip.auth_service.models.UserRepository;
import com.antrip.auth_service.utils.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public void register(String displayName, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + email + " already exists");
        }

        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        user.setDisplayName(displayName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setDateCreated(now);
        user.setDateUpdated(now);
        userRepository.save(user);
    }

    @Override
    public String login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        return jwtUtil.generateToken(authentication.getName());
    }
}
