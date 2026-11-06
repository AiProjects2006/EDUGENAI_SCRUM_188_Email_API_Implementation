package com.ailearning.emailapi.service;

import com.ailearning.emailapi.dto.EmailRequest;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(EmailRequest request) {

        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setTo(request.to());
        mail.setSubject(request.subject());
        mail.setText(request.message());

        mailSender.send(mail);
    }
}