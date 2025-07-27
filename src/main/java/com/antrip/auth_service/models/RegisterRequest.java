package com.antrip.auth_service.models;

import com.antrip.auth_service.exceptions.InvalidAuthRequestException;
import io.micrometer.common.util.StringUtils;

public record RegisterRequest(String displayName, String email, String password) {

    public RegisterRequest {
        if (StringUtils.isBlank(displayName)) {
            throw new InvalidAuthRequestException("Display name cannot be null or blank", "displayName");
        }
        if (StringUtils.isBlank(email)) {
            throw new InvalidAuthRequestException("Email cannot be null or blank", "email");
        }
        if (StringUtils.isBlank(password)) {
            throw new InvalidAuthRequestException("Password cannot be null or blank", "password");
        }
    }
}
