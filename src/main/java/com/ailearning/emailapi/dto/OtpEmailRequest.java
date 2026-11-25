
package com.ailearning.emailapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record OtpEmailRequest(

        @NotBlank(message = "Recipient email is required")
        @Email(message = "Invalid recipient email")
        String to,

        @NotBlank(message = "OTP is required")
        @Pattern(
                regexp = "\\d{6}",
                message = "OTP must contain exactly six numeric digits"
        )
        String otp,

        @NotNull(message = "Purpose must be REGISTRATION or PASSWORD_RESET")
        OtpPurpose purpose,

        @NotNull(message = "Expiry minutes is required")
        @Min(value = 1, message = "Expiry minutes must be at least 1")
        @Max(value = 30, message = "Expiry minutes cannot exceed 30")
        Integer expiryMinutes

) {
}
