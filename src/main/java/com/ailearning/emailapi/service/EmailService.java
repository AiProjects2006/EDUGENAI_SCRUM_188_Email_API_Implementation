package com.ailearning.emailapi.service;

import com.ailearning.emailapi.dto.EmailRequest;
import com.ailearning.emailapi.dto.OtpEmailRequest;
import com.ailearning.emailapi.dto.OtpPurpose;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger log =
            LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(EmailRequest request) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(request.to());
        mail.setSubject(request.subject());
        mail.setText(request.message());

        try {
            mailSender.send(mail);
            log.info("Email successfully sent to recipient.");
        } catch (MailException ex) {
            log.error("SMTP email delivery failed.");
            throw ex;
        }
    }

    public void sendOtpEmail(OtpEmailRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setTo(request.to());
            helper.setSubject(subjectFor(request.purpose()));
            helper.setText(buildOtpHtml(request), true);

            mailSender.send(message);
            log.info("OTP email sent successfully.");
        } catch (MailException ex) {
            log.error("OTP email delivery failed.");
            throw ex;
        } catch (Exception ex) {
            log.error("Unable to construct OTP email.");
            throw new org.springframework.mail.MailSendException("Unable to construct OTP email.");
        }
    }

    private String subjectFor(OtpPurpose purpose) {
        return purpose == OtpPurpose.REGISTRATION
                ? "EDUGENAI - Email Verification Code"
                : "EDUGENAI - Password Reset Verification Code";
    }

    private String buildOtpHtml(OtpEmailRequest request) {
        String safeOtp = HtmlUtils.htmlEscape(request.otp());
        String expiry = HtmlUtils.htmlEscape(request.expiryMinutes().toString());
        boolean registration = request.purpose() == OtpPurpose.REGISTRATION;

        String intro = registration
                ? "Thank you for registering with EDUGENAI!"
                : "We received a request to reset your EDUGENAI account password.";
        String ignoreText = registration
                ? "If you did not request this verification code, please ignore this email."
                : "If you did not request a password reset, please ignore this email. Your account password will remain unchanged.";

        return """
                <!doctype html>
                <html>
                  <body style="margin:0;padding:24px;background:#f4f7fb;font-family:Arial,sans-serif;color:#1f2937;">
                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0">
                      <tr><td align="center">
                        <table role="presentation" width="600" cellspacing="0" cellpadding="32"
                               style="max-width:600px;width:100%%;background:#ffffff;border-radius:12px;">
                          <tr><td>
                            <h1 style="margin:0 0 20px;color:#2563eb;font-size:24px;">EDUGENAI</h1>
                            <p>Hello,</p>
                            <p>%s</p>
                            <p>Your verification code is:</p>
                            <div style="margin:24px 0;padding:18px;background:#eff6ff;border-radius:8px;text-align:center;
                                        color:#1d4ed8;font-size:32px;font-weight:bold;letter-spacing:8px;">
                              %s
                            </div>
                            <p>This verification code will expire in %s minutes.</p>
                            <p><strong>Please do not share this code with anyone.</strong></p>
                            <p>%s</p>
                            <p>Regards,<br>EDUGENAI Team</p>
                          </td></tr>
                        </table>
                      </td></tr>
                    </table>
                  </body>
                </html>
                """.formatted(intro, safeOtp, expiry, ignoreText);
    }
}
