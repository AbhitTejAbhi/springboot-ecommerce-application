package com.ecommerce.backend.exception;

/**
 * Thrown when a request is well-formed but violates a business rule —
 * e.g. registering with an email that's already taken, logging in with
 * invalid credentials, ordering more stock than is available, checking
 * out an empty cart, or attempting an invalid order state transition.
 *
 * Handled by GlobalExceptionHandler and mapped to HTTP 400 Bad Request.
 *
 * Usage:
 *   throw new BadRequestException("Email already exists: " + email);
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}