package com.examly.springapp.exceptions;

/** A request that is well-formed JSON but breaks a business rule. Answered with HTTP 400 and the message. */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
