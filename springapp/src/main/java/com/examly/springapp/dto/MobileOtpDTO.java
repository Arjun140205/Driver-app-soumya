package com.examly.springapp.dto;

import com.examly.springapp.dto.validation.OnVerify;
import com.examly.springapp.dto.validation.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/** Request body of POST /api/otp/mobile/send (only "mobileNumber") and /api/otp/mobile/verify ("mobileNumber" + "otp"). */
public class MobileOtpDTO {

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = ValidationPatterns.MOBILE, message = "Mobile number must be 10 digits")
    private String mobileNumber;

    @NotNull(groups = OnVerify.class, message = "OTP is required")
    @Pattern(regexp = ValidationPatterns.OTP, message = "OTP must be 6 digits")
    private String otp;

    public MobileOtpDTO() {
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
