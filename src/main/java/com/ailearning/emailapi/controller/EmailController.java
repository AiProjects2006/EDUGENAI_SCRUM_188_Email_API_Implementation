package com.ailearning.emailapi.controller;

import com.ailearning.emailapi.dto.EmailRequest;
import com.ailearning.emailapi.dto.EmailResponse;
import com.ailearning.emailapi.service.EmailService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<EmailResponse> sendEmail(
            @Valid @RequestBody EmailRequest request) {

        emailService.sendEmail(request);

        EmailResponse response = new EmailResponse(
                "SUCCESS",
                "Email sent successfully"
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}