package com.ailearning.emailapi.service;

import com.ailearning.emailapi.dto.EmailRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

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
}