package com.ailearning.emailapi.controller;

import com.ailearning.emailapi.dto.EmailRequest;
import com.ailearning.emailapi.dto.EmailResponse;
import com.ailearning.emailapi.dto.OtpEmailRequest;
import com.ailearning.emailapi.service.EmailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Send a general-purpose email")
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

    @PostMapping("/send-otp")
    @Operation(summary = "Send a registration or password-reset OTP email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OTP email sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid OTP email request"),
            @ApiResponse(responseCode = "502", description = "SMTP email delivery failure")
    })
    public ResponseEntity<EmailResponse> sendOtp(
            @Valid @RequestBody OtpEmailRequest request) {

        emailService.sendOtpEmail(request);
        return ResponseEntity.ok(
                new EmailResponse("SUCCESS", "OTP email sent successfully")
        );
    }
}