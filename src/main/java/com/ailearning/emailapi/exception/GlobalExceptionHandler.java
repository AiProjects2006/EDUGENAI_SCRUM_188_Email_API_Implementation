package com.ailearning.emailapi.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        errors.put("status", "FAILED");
        errors.put("message", "Invalid request");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleUnreadableRequest(
            HttpMessageNotReadableException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("status", "FAILED");
        error.put("message", "Invalid request");
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MailAuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleMailAuthentication(
            MailAuthenticationException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("status", "FAILED");
        error.put("message", "SMTP authentication failed");
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }

    @ExceptionHandler(MailSendException.class)
    public ResponseEntity<Map<String, String>> handleMailSend(
            MailSendException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("status", "FAILED");
        error.put("message", "Email delivery failed");
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<Map<String, String>> handleMailError(
            MailException ex) {

        Map<String, String> error = new HashMap<>();

        error.put("status", "FAILED");
        error.put("message", "Email delivery failed");

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(error);
    }
}