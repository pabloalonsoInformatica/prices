package com.inditex.technicaltest.prices.controllers;


import com.inditex.technicaltest.prices.models.AppError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.time.LocalDateTime;

@RestControllerAdvice
public class HandlerExceptionController {
    private static final Logger logger = LoggerFactory.getLogger(HandlerExceptionController.class);
    @ExceptionHandler()
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<AppError> internalException(Exception ex) {
        AppError error = new AppError();
        error.setDate(LocalDateTime.now());
        error.setError("Internal Error"); // hide internal errors TODO [pablo.alonso 20241007] show in non-production environments
        error.setMessage("Internal Error");
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        logger.error(ex.getMessage() + ". error: {}", error);
        return ResponseEntity.internalServerError().body(error);
    }
}
