package com.sidney.banking.auth.api;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.core.AuthenticationException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBusinessError(
            IllegalArgumentException exception
    ) {
        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                Map.of(),
                OffsetDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationError(
            AuthenticationException exception
    ) {
        ApiError error = new ApiError(
                HttpStatus.UNAUTHORIZED.value(),
                "E-mail ou senha inválidos",
                Map.of(),
                OffsetDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationError(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> fields = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        fields.putIfAbsent(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Dados inválidos",
                fields,
                OffsetDateTime.now()
        );

        return ResponseEntity
                .badRequest()
                .body(error);
    }
}