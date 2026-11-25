package com.ailearning.emailapi.service;

import com.ailearning.emailapi.dto.EmailRequest;
import com.ailearning.emailapi.dto.OtpEmailRequest;
import com.ailearning.emailapi.dto.OtpPurpose;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.MailSendException;

import jakarta.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void shouldSendEmailSuccessfully() {

        EmailRequest request = new EmailRequest(
                "student@gmail.com",
                "Test Subject",
                "Test Message"
        );

        emailService.sendEmail(request);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        SimpleMailMessage sentMail = captor.getValue();

        assertEquals(
                "student@gmail.com",
                sentMail.getTo()[0]
        );

        assertEquals(
                "Test Subject",
                sentMail.getSubject()
        );

        assertEquals(
                "Test Message",
                sentMail.getText()
        );
    }

    @Test
    void shouldSendRegistrationOtpEmailSuccessfully() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        OtpEmailRequest request = new OtpEmailRequest(
                "student@gmail.com", "483921", OtpPurpose.REGISTRATION, 5);

        emailService.sendOtpEmail(request);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void shouldSendPasswordResetOtpEmailSuccessfully() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        OtpEmailRequest request = new OtpEmailRequest(
                "student@gmail.com", "483921", OtpPurpose.PASSWORD_RESET, 5);

        emailService.sendOtpEmail(request);

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void shouldPropagateSmtpFailure() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("SMTP failure"))
                .when(mailSender).send(mimeMessage);

        OtpEmailRequest request = new OtpEmailRequest(
                "student@gmail.com", "483921", OtpPurpose.REGISTRATION, 5);

        assertThrows(MailSendException.class,
                () -> emailService.sendOtpEmail(request));
    }
}