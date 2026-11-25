
package com.ailearning.emailapi.controller;

import com.ailearning.emailapi.dto.EmailRequest;
import com.ailearning.emailapi.dto.OtpEmailRequest;
import com.ailearning.emailapi.exception.GlobalExceptionHandler;
import com.ailearning.emailapi.service.EmailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.mail.MailSendException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmailControllerTest {

    private EmailService emailService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        emailService = mock(EmailService.class);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new EmailController(emailService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // 1. Registration OTP
    @Test
    void shouldSendRegistrationOtp() throws Exception {

        mockMvc.perform(post("/api/email/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOtpJson("REGISTRATION")))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message")
                        .value("OTP email sent successfully"));

        verify(emailService)
                .sendOtpEmail(any(OtpEmailRequest.class));
    }

    // 2. Password reset OTP
    @Test
    void shouldSendPasswordResetOtp() throws Exception {

        mockMvc.perform(post("/api/email/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOtpJson("PASSWORD_RESET")))

                .andExpect(status().isOk());

        verify(emailService)
                .sendOtpEmail(any(OtpEmailRequest.class));
    }

    // 3. Existing general email endpoint
    @Test
    void shouldPreserveGeneralEmailEndpoint() throws Exception {

        mockMvc.perform(post("/api/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "to": "student@gmail.com",
                                    "subject": "Test",
                                    "message": "Hello"
                                }
                                """))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(emailService)
                .sendEmail(any(EmailRequest.class));
    }

    // 4. Invalid email
    @Test
    void shouldRejectInvalidEmail() throws Exception {

        mockMvc.perform(post("/api/email/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOtpJson("REGISTRATION")
                                .replace(
                                        "student@gmail.com",
                                        "not-an-email"
                                )))

                .andExpect(status().isBadRequest());
    }

    // 5. Missing OTP
    @Test
    void shouldRejectMissingOtp() throws Exception {

        mockMvc.perform(post("/api/email/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "to": "student@gmail.com",
                                    "purpose": "REGISTRATION",
                                    "expiryMinutes": 5
                                }
                                """))

                .andExpect(status().isBadRequest());
    }

    // 6. OTP containing letters
    @Test
    void shouldRejectLettersInOtp() throws Exception {

        assertInvalidOtp("12AB56");
    }

    // 7. OTP shorter than six digits
    @Test
    void shouldRejectShortOtp() throws Exception {

        assertInvalidOtp("12345");
    }

    // 8. OTP longer than six digits
    @Test
    void shouldRejectLongOtp() throws Exception {

        assertInvalidOtp("1234567");
    }

    // 9. Invalid purpose
    @Test
    void shouldRejectInvalidPurpose() throws Exception {

        mockMvc.perform(post("/api/email/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOtpJson("UNKNOWN")))

                .andExpect(status().isBadRequest());
    }

    // 10. Invalid expiry
    
@Test
void shouldRejectInvalidExpiry() throws Exception {

    String invalidExpiryJson = """
            {
                "to": "student@gmail.com",
                "otp": "483921",
                "purpose": "REGISTRATION",
                "expiryMinutes": 31
            }
            """;

    mockMvc.perform(post("/api/email/send-otp")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidExpiryJson))
            .andExpect(status().isBadRequest());
}

    // 11. SMTP failure
    @Test
    void shouldReturnBadGatewayForSmtpFailure() throws Exception {

        doThrow(new MailSendException("Internal SMTP details"))
                .when(emailService)
                .sendOtpEmail(any(OtpEmailRequest.class));

        mockMvc.perform(post("/api/email/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOtpJson("REGISTRATION")))

                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.message")
                        .value("Email delivery failed"));
    }

    // Common invalid OTP test helper
    private void assertInvalidOtp(String otp) throws Exception {

        mockMvc.perform(post("/api/email/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOtpJson("REGISTRATION")
                                .replace("483921", otp)))

                .andExpect(status().isBadRequest());
    }

    // Common valid OTP JSON helper
    private String validOtpJson(String purpose) {

        return """
                {
                    "to": "student@gmail.com",
                    "otp": "483921",
                    "purpose": "%s",
                    "expiryMinutes": 5
                }
                """.formatted(purpose);
    }
}
