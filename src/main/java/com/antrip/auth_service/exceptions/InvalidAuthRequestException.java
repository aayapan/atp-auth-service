package com.antrip.auth_service.exceptions;

import lombok.Getter;

@Getter
public class InvalidAuthRequestException extends RuntimeException {

    private final String field;

    public InvalidAuthRequestException(String message, String field) {
        super(message);
        this.field = field;
    }
}
