package com.ecommerce.backend.exception;

/**
 * Thrown when a requested resource does not exist — e.g. a User,
 * Product, Order, Category, or Address lookup by id (or other
 * unique identifier) finds no matching record.
 *
 * Handled by GlobalExceptionHandler and mapped to HTTP 404 Not Found.
 *
 * Usage:
 *   throw new ResourceNotFoundException("User not found with id: " + id);
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}