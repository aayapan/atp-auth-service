package com.antrip.auth_service.models;

import com.antrip.auth_service.exceptions.InvalidRegisterRequestException;
import io.micrometer.common.util.StringUtils;

public record RegisterRequest(String displayName, String email, String password) {

    public RegisterRequest {
        if (StringUtils.isBlank(displayName)) {
            throw new InvalidRegisterRequestException("Display name cannot be null or blank", "displayName");
        }
        if (StringUtils.isBlank(email)) {
            throw new InvalidRegisterRequestException("Email cannot be null or blank", "email");
        }
        if (StringUtils.isBlank(password)) {
            throw new InvalidRegisterRequestException("Password cannot be null or blank", "password");
        }
    }
}
