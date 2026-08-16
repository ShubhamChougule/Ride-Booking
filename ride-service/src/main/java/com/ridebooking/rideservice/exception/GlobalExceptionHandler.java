package com.ridebooking.rideservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        FieldError firstError = ex.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(!Objects.isNull(firstError) ? firstError.getDefaultMessage() : "Something went wrong");
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleExternalAPIErrorException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(!Objects.isNull(ex.getMessage()) ? ex.getMessage() : "Something went wrong");
    }

}
