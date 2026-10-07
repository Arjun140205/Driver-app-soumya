package com.examly.springapp.service;

import com.examly.springapp.exceptions.OtpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Keeps the OTPs in memory (they are short lived, so no table is needed; a server restart simply
 * means "request a new OTP").
 *
 * Rules: 6 digits from a SecureRandom, valid for otp.expiry-minutes, at most otp.max-attempts wrong
 * guesses, a new OTP can only be requested every otp.resend-cooldown-seconds, and a verified
 * e-mail / number stays verified for otp.verified-validity-minutes (long enough to finish the form).
 */
@Service
public class OtpServiceImpl implements OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpServiceImpl.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String EMAIL = "EMAIL";
    private static final String MOBILE = "MOBILE";

    @Autowired
    private EmailOtpSender emailSender;

    @Autowired
    private SmsOtpSender smsSender;

    @Value("${otp.expiry-minutes:5}")
    private long expiryMinutes;

    @Value("${otp.resend-cooldown-seconds:30}")
    private long resendCooldownSeconds;

    @Value("${otp.max-attempts:5}")
    private int maxAttempts;

    @Value("${otp.verified-validity-minutes:15}")
    private long verifiedValidityMinutes;

    @Value("${otp.dev-mode:true}")
    private boolean devMode;

    /** OTPs that were sent but not verified yet. */
    private final Map<String, PendingOtp> pending = new HashMap<>();

    /** E-mails / numbers that were verified, with the moment the verification stops counting. */
    private final Map<String, Instant> verified = new HashMap<>();

    private static final class PendingOtp {
        private final String code;
        private final Instant expiresAt;
        private final Instant sentAt;
        private int attempts;

        private PendingOtp(String code, Instant expiresAt, Instant sentAt) {
            this.code = code;
            this.expiresAt = expiresAt;
            this.sentAt = sentAt;
        }
    }

    // ------------------------------------------------------------------ send

    @Override
    public String sendEmailOtp(String email) {
        return send(EMAIL, emailKey(email), email);
    }

    @Override
    public String sendMobileOtp(String mobileNumber) {
        return send(MOBILE, mobileKey(mobileNumber), mobileNumber.trim());
    }

    private synchronized String send(String kind, String key, String target) {
        purgeExpired();
        Instant now = Instant.now();

        PendingOtp previous = pending.get(key);
        if (previous != null) {
            Instant nextAllowed = previous.sentAt.plusSeconds(resendCooldownSeconds);
            if (now.isBefore(nextAllowed)) {
                long wait = Duration.between(now, nextAllowed).toSeconds() + 1;
                throw new OtpException(HttpStatus.TOO_MANY_REQUESTS,
                        "Please wait " + wait + " seconds before requesting another OTP.");
            }
        }

        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        String delivery = deliver(kind, target, code);

        // stored only after the delivery worked; a new OTP restarts the verification
        pending.put(key, new PendingOtp(code, now.plus(Duration.ofMinutes(expiryMinutes)), now));
        verified.remove(key);
        return delivery;
    }

    private String deliver(String kind, String target, String code) {
        boolean email = EMAIL.equals(kind);
        boolean configured = email ? emailSender.isConfigured() : smsSender.isConfigured();
        if (configured) {
            if (email) {
                emailSender.send(target, code);
                return "email";
            }
            smsSender.send(target, code);
            return "sms";
        }
        if (!devMode) {
            throw new OtpException(HttpStatus.SERVICE_UNAVAILABLE,
                    (email ? "E-mail" : "SMS") + " OTP delivery is not configured on the server.");
        }
        // Development fallback: nothing is sent, the code is printed here (and never returned to the browser).
        log.warn("[OTP DEV MODE] {} is not configured - the OTP for {} {} is {}",
                email ? "SMTP" : "SMS provider", kind.toLowerCase(Locale.ROOT), target, code);
        return "console";
    }

    // ------------------------------------------------------------------ verify

    @Override
    public void verifyEmailOtp(String email, String otp) {
        verify(emailKey(email), otp, "e-mail address");
    }

    @Override
    public void verifyMobileOtp(String mobileNumber, String otp) {
        verify(mobileKey(mobileNumber), otp, "mobile number");
    }

    private synchronized void verify(String key, String otp, String label) {
        purgeExpired();
        PendingOtp entry = pending.get(key);
        if (entry == null) {
            throw new OtpException(HttpStatus.BAD_REQUEST,
                    "This OTP is no longer valid. Please request a new OTP for your " + label + ".");
        }
        boolean matches = MessageDigest.isEqual(
                entry.code.getBytes(StandardCharsets.UTF_8), otp.getBytes(StandardCharsets.UTF_8));
        if (!matches) {
            entry.attempts++;
            if (entry.attempts >= maxAttempts) {
                pending.remove(key);
                throw new OtpException(HttpStatus.TOO_MANY_REQUESTS,
                        "Too many incorrect attempts. Please request a new OTP.");
            }
            int left = maxAttempts - entry.attempts;
            throw new OtpException(HttpStatus.BAD_REQUEST,
                    "Incorrect OTP. " + left + (left == 1 ? " attempt" : " attempts") + " left.");
        }
        pending.remove(key);
        verified.put(key, Instant.now().plus(Duration.ofMinutes(verifiedValidityMinutes)));
    }

    // ------------------------------------------------------------------ verified state

    @Override
    public synchronized boolean isEmailVerified(String email) {
        return isVerified(emailKey(email));
    }

    @Override
    public synchronized boolean isMobileVerified(String mobileNumber) {
        return isVerified(mobileKey(mobileNumber));
    }

    private boolean isVerified(String key) {
        Instant until = verified.get(key);
        return until != null && Instant.now().isBefore(until);
    }

    @Override
    public synchronized void clearVerification(String email, String mobileNumber) {
        verified.remove(emailKey(email));
        verified.remove(mobileKey(mobileNumber));
    }

    // ------------------------------------------------------------------ helpers

    private void purgeExpired() {
        Instant now = Instant.now();
        pending.values().removeIf(entry -> now.isAfter(entry.expiresAt));
        verified.values().removeIf(now::isAfter);
    }

    private static String emailKey(String email) {
        return EMAIL + ":" + (email == null ? "" : email.trim().toLowerCase(Locale.ROOT));
    }

    private static String mobileKey(String mobileNumber) {
        return MOBILE + ":" + (mobileNumber == null ? "" : mobileNumber.trim());
    }
}
