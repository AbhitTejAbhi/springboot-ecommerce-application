package com.ecommerce.backend.enums;

/**
 * Status lifecycle of an IdempotencyKey record:
 * - PROCESSING: Request is currently being executed by the server
 * - COMPLETED : Request finished successfully and its response is cached
 * - FAILED    : Request processing encountered a system error
 */
public enum IdempotencyKeyStatus {
    PROCESSING,
    COMPLETED,
    FAILED
}
