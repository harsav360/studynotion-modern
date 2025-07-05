package com.studynotion_modern.backend.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        var bindingResult = ex.getBindingResult();
        if (bindingResult != null) {
            bindingResult.getFieldErrors().forEach(error -> {
                String field = error.getField();
                String message = error.getDefaultMessage();
                errors.put(field, message);
            });
        }

        return ResponseEntity.badRequest().body(errors);
    }
}
