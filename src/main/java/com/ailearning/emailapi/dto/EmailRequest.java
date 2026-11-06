package com.ailearning.emailapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmailRequest(

        @NotNull(message = "Recipient email is required")
        @NotBlank(message = "Recipient email cannot be empty")
        @Email(message = "Invalid email address")
        String to,

        @NotBlank(message = "Subject is required")
        String subject,

        @NotBlank(message = "Message is required")
        String message

) {}