package com.examly.springapp.controller;

import com.examly.springapp.dto.EmailOtpDTO;
import com.examly.springapp.dto.MobileOtpDTO;
import com.examly.springapp.dto.validation.OnVerify;
import com.examly.springapp.service.OtpService;
import com.examly.springapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * Public endpoints used by the sign-up page to verify an e-mail address and a mobile number.
 * The OTP itself is never returned; "delivery" only tells the page where it went
 * ("email", "sms", or "console" when no provider is configured in development mode).
 */
@RestController
@RequestMapping("/api/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserService userService;

    @PostMapping("/email/send")
    public ResponseEntity<?> sendEmailOtp(@Valid @RequestBody EmailOtpDTO request) {
        if (userService.emailExists(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "A user with this email already exists"));
        }
        String delivery = otpService.sendEmailOtp(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "OTP sent to " + request.getEmail().trim(), "delivery", delivery));
    }

    @PostMapping("/email/verify")
    public ResponseEntity<?> verifyEmailOtp(@Validated(OnVerify.class) @RequestBody EmailOtpDTO request) {
        otpService.verifyEmailOtp(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(Map.of("verified", true, "message", "Email verified"));
    }

    @PostMapping("/mobile/send")
    public ResponseEntity<?> sendMobileOtp(@Valid @RequestBody MobileOtpDTO request) {
        String delivery = otpService.sendMobileOtp(request.getMobileNumber());
        return ResponseEntity.ok(Map.of("message", "OTP sent to " + request.getMobileNumber().trim(), "delivery", delivery));
    }

    @PostMapping("/mobile/verify")
    public ResponseEntity<?> verifyMobileOtp(@Validated(OnVerify.class) @RequestBody MobileOtpDTO request) {
        otpService.verifyMobileOtp(request.getMobileNumber(), request.getOtp());
        return ResponseEntity.ok(Map.of("verified", true, "message", "Mobile number verified"));
    }
}
