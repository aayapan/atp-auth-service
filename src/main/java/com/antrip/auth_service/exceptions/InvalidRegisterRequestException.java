package com.antrip.auth_service.exceptions;

import lombok.Getter;

@Getter
public class InvalidRegisterRequestException extends RuntimeException {

    private final String field;

    public InvalidRegisterRequestException(String message, String field) {
        super(message);
        this.field = field;
    }
}
