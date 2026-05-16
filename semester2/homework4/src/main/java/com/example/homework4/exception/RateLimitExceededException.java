package com.example.homework4.exception;

public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
