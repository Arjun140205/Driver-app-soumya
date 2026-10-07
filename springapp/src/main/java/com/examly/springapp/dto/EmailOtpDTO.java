package com.examly.springapp.dto;

import com.examly.springapp.dto.validation.OnVerify;
import com.examly.springapp.dto.validation.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/** Request body of POST /api/otp/email/send (only "email") and /api/otp/email/verify ("email" + "otp"). */
public class EmailOtpDTO {

    @NotBlank(message = "Email is required")
    @Pattern(regexp = ValidationPatterns.EMAIL, message = "Please enter a valid email address")
    private String email;

    @NotNull(groups = OnVerify.class, message = "OTP is required")
    @Pattern(regexp = ValidationPatterns.OTP, message = "OTP must be 6 digits")
    private String otp;

    public EmailOtpDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
