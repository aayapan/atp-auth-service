package com.antrip.auth_service.models;

import io.micrometer.common.util.StringUtils;

public record RegisterRequest(String displayName, String email, String password) {

    public RegisterRequest {
        if (StringUtils.isBlank(displayName)) {
            throw new IllegalArgumentException("Display name cannot be null or blank");
        }
        if (StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
    }
}
