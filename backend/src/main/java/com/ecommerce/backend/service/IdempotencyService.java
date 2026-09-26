package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.response.IdempotencyResult;

import java.util.function.Supplier;

public interface IdempotencyService {

    /**
     * Executes the given action idempotently.
     *
     * @param idempotencyKey  Client-supplied UUID header (or null if un-idempotent)
     * @param userId          Logged-in user ID
     * @param endpoint        Target URI path (e.g. "/api/customer/orders")
     * @param requestPayload  Request DTO object to hash
     * @param responseClass   Target response class type for JSON deserialization
     * @param action          Business logic execution lambda
     * @return IdempotencyResult wrapping HTTP status, response body, and cached flag
     */
    <T> IdempotencyResult<T> execute(
            String idempotencyKey,
            Long userId,
            String endpoint,
            Object requestPayload,
            Class<T> responseClass,
            Supplier<T> action
    );
}
