package com.examly.springapp.service;

import com.examly.springapp.exceptions.OtpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

/**
 * Delivers an OTP by SMS through the Twilio REST API (no extra library needed: it is one HTTPS POST).
 * Configure otp.sms.twilio.account-sid / auth-token / from-number (TWILIO_* environment variables).
 * To use another SMS provider, only this class has to change.
 */
@Component
public class SmsOtpSender {

    private static final Logger log = LoggerFactory.getLogger(SmsOtpSender.class);

    @Value("${otp.sms.twilio.account-sid:}")
    private String accountSid;

    @Value("${otp.sms.twilio.auth-token:}")
    private String authToken;

    @Value("${otp.sms.twilio.from-number:}")
    private String fromNumber;

    @Value("${otp.sms.country-code:+91}")
    private String countryCode;

    @Value("${otp.expiry-minutes:5}")
    private long expiryMinutes;

    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    public boolean isConfigured() {
        return notBlank(accountSid) && notBlank(authToken) && notBlank(fromNumber);
    }

    public void send(String mobileNumber, String code) {
        String body = form("To", countryCode + mobileNumber)
                + "&" + form("From", fromNumber)
                + "&" + form("Body", "Your DriveU verification code is " + code
                        + ". It is valid for " + expiryMinutes + " minutes. Do not share it with anyone.");
        String credentials = Base64.getEncoder().encodeToString((accountSid + ":" + authToken).getBytes(StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.newBuilder(
                        URI.create("https://api.twilio.com/2010-04-01/Accounts/" + accountSid + "/Messages.json"))
                .timeout(Duration.ofSeconds(10))
                .header("Authorization", "Basic " + credentials)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                log.error("Twilio refused the OTP SMS (HTTP {}): {}", response.statusCode(), response.body());
                throw new OtpException(HttpStatus.BAD_GATEWAY, "We could not send the OTP SMS. Please try again later.");
            }
        } catch (IOException e) {
            log.error("Could not reach Twilio: {}", e.getMessage());
            throw new OtpException(HttpStatus.BAD_GATEWAY, "We could not send the OTP SMS. Please try again later.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OtpException(HttpStatus.BAD_GATEWAY, "We could not send the OTP SMS. Please try again later.");
        }
    }

    private static String form(String key, String value) {
        return URLEncoder.encode(key, StandardCharsets.UTF_8) + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }
}
