package com.awesome.booking.pizza.api.exception;

import com.awesome.booking.pizza.lib.ErrorResponse;
import com.awesome.booking.pizza.lib.exception.ConflictException;
import com.awesome.booking.pizza.lib.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(ResourceNotFoundException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse()
                .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""));
        log.warn("Entity not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse()
                .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""));
        log.info("Conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Validation error: {}", ex.getMessage(), ex);

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        ErrorResponse error = new ErrorResponse()
                .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(message)
                .path(request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse()
                .timestamp(OffsetDateTime.now(ZoneOffset.UTC))
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""));
        log.error("Unexpected error at: {}: {}", request.getDescription(false), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
