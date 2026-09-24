package com.ecommerce.backend.exception;

/**
 * Thrown when an HTTP request conflicts with current server state —
 * e.g. an idempotency key is reused with a different request payload,
 * or a request with the same idempotency key is currently being processed.
 *
 * Handled by GlobalExceptionHandler and mapped to HTTP 409 Conflict.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
