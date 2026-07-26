package com.sidney.banking.account.api;

import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(
            MethodArgumentNotValidException exception
    ) {

        Map<String, String> fields =
                exception.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .collect(
                                java.util.stream.Collectors.toMap(
                                        fieldError ->
                                                fieldError.getField(),
                                        fieldError ->
                                                fieldError.getDefaultMessage(),
                                        (first, second) -> first
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

    public record ApiError(
            int status,
            String message,
            Map<String, String> fields,
            OffsetDateTime timestamp
    ) {
    }
}