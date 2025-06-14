package com.antrip.auth_service.services;

public interface AuthService {

    void register(String displayName, String email, String password);

    String login(String email, String password);
}
