package com.antrip.auth_service.controllers;

import com.antrip.auth_service.models.AuthResponse;
import com.antrip.auth_service.models.RegisterRequest;
import com.antrip.auth_service.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        authService.register(request.displayName(), request.email(), request.password());
        String token = authService.login(request.email(), request.password());
        return new ResponseEntity<>(new AuthResponse(token), HttpStatus.CREATED);
    }
}
