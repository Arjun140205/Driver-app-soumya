package com.examly.springapp.exceptions;

import org.springframework.http.HttpStatus;

/** Raised by the OTP flow (wrong / expired code, too many attempts, delivery failure ...). */
public class OtpException extends RuntimeException {

    private final HttpStatus status;

    public OtpException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
