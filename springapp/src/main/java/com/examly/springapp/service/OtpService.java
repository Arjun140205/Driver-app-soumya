package com.examly.springapp.service;

/**
 * One-time passwords used to verify an e-mail address and a mobile number during sign-up.
 * A "send" creates a 6 digit code and delivers it (SMTP e-mail / SMS); a successful "verify" marks
 * that e-mail or number as verified, and registration is only allowed for verified ones.
 */
public interface OtpService {

    /** Sends an OTP to the e-mail address. Returns how it was delivered: "email" or "console" (dev mode). */
    String sendEmailOtp(String email);

    /** Sends an OTP to the mobile number. Returns how it was delivered: "sms" or "console" (dev mode). */
    String sendMobileOtp(String mobileNumber);

    /** Checks the code; throws {@link com.examly.springapp.exceptions.OtpException} when it is wrong, expired or used up. */
    void verifyEmailOtp(String email, String otp);

    void verifyMobileOtp(String mobileNumber, String otp);

    boolean isEmailVerified(String email);

    boolean isMobileVerified(String mobileNumber);

    /** Forgets the verification of this e-mail and number (called once the account has been created). */
    void clearVerification(String email, String mobileNumber);
}
