package com.antrip.auth_service.controllers;

import com.antrip.auth_service.exceptions.InvalidRegisterRequestException;
import com.antrip.auth_service.models.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(InvalidRegisterRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRegisterRequest(InvalidRegisterRequestException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getField());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
