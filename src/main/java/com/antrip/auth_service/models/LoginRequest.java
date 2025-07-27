package com.antrip.auth_service.models;

import com.antrip.auth_service.exceptions.InvalidAuthRequestException;
import io.micrometer.common.util.StringUtils;

public record LoginRequest(String email, String password) {

    public LoginRequest {
        if (StringUtils.isBlank(email)) {
            throw new InvalidAuthRequestException("Email cannot be null or blank", "email");
        }
        if (StringUtils.isBlank(password)) {
            throw new InvalidAuthRequestException("Password cannot be null or blank", "password");
        }
    }
}
