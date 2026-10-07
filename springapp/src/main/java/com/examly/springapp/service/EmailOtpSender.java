package com.examly.springapp.service;

import com.examly.springapp.exceptions.OtpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Delivers an OTP by e-mail over SMTP (spring.mail.* in application.properties).
 * It counts as configured once a mail username is set (MAIL_USERNAME / MAIL_PASSWORD).
 */
@Component
public class EmailOtpSender {

    private static final Logger log = LoggerFactory.getLogger(EmailOtpSender.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${app.mail.from:}")
    private String from;

    @Value("${otp.expiry-minutes:5}")
    private long expiryMinutes;

    public boolean isConfigured() {
        return mailSender != null && mailUsername != null && !mailUsername.isBlank();
    }

    public void send(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from == null || from.isBlank() ? mailUsername : from);
        message.setTo(to);
        message.setSubject("Your DriveU verification code");
        message.setText("Your DriveU verification code is " + code + ".\n\n"
                + "It is valid for " + expiryMinutes + " minutes. Do not share it with anyone.\n"
                + "If you did not try to sign up for DriveU, you can ignore this e-mail.");
        try {
            mailSender.send(message);
        } catch (MailException e) {
            log.error("Could not send the OTP e-mail: {}", e.getMessage());
            throw new OtpException(HttpStatus.BAD_GATEWAY, "We could not send the OTP e-mail. Please try again later.");
        }
    }
}
