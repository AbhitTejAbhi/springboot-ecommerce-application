package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.response.IdempotencyResult;
import com.ecommerce.backend.entity.IdempotencyKey;
import com.ecommerce.backend.enums.IdempotencyKeyStatus;
import com.ecommerce.backend.exception.ConflictException;
import com.ecommerce.backend.repository.IdempotencyKeyRepository;
import com.ecommerce.backend.service.IdempotencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyServiceImpl implements IdempotencyService {

    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final ObjectMapper objectMapper;

    @Override
    public <T> IdempotencyResult<T> execute(
            String idempotencyKey,
            Long userId,
            String endpoint,
            Object requestPayload,
            Class<T> responseClass,
            Supplier<T> action) {

        // If no idempotency key is provided, execute action directly
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            T result = action.get();
            return IdempotencyResult.<T>builder()
                    .cached(false)
                    .httpStatus(201)
                    .body(result)
                    .build();
        }

        String requestHash = computeHash(requestPayload);

        Optional<IdempotencyKey> existingOpt = idempotencyKeyRepository
                .findByIdempotencyKeyAndUserIdAndEndpoint(idempotencyKey, userId, endpoint);

        if (existingOpt.isPresent()) {
            IdempotencyKey existing = existingOpt.get();

            if (existing.getStatus() == IdempotencyKeyStatus.FAILED) {
                log.info("Removing previously FAILED idempotency record key={} so user can retry", idempotencyKey);
                idempotencyKeyRepository.delete(existing);
            } else {
                // 1. Payload Mismatch Check
                if (!existing.getRequestHash().equals(requestHash)) {
                    log.warn("Idempotency key conflict: key={} userId={} endpoint={} (Payload hash mismatch)",
                            idempotencyKey, userId, endpoint);
                    throw new ConflictException("Idempotency key has already been used with a different request payload.");
                }

                // 2. Processing State Check
                if (existing.getStatus() == IdempotencyKeyStatus.PROCESSING) {
                    log.warn("Idempotency key processing conflict: key={} userId={} endpoint={}",
                            idempotencyKey, userId, endpoint);
                    throw new ConflictException("A request with this idempotency key is currently being processed.");
                }

                // 3. Completed State -> Return cached response
                if (existing.getStatus() == IdempotencyKeyStatus.COMPLETED) {
                    log.info("Idempotency cache hit: key={} userId={} endpoint={} -> Returning stored response (status={})",
                            idempotencyKey, userId, endpoint, existing.getResponseStatus());
                    try {
                        T cachedBody = objectMapper.readValue(existing.getResponseBody(), responseClass);
                        return IdempotencyResult.<T>builder()
                                .cached(true)
                                .httpStatus(existing.getResponseStatus())
                                .body(cachedBody)
                                .build();
                    } catch (Exception e) {
                        log.error("Failed to deserialize cached idempotency response body", e);
                        throw new RuntimeException("Failed to read cached idempotency response", e);
                    }
                }
            }
        }

        // First execution attempt -> Create key in PROCESSING state
        IdempotencyKey newRecord = IdempotencyKey.builder()
                .idempotencyKey(idempotencyKey)
                .userId(userId)
                .endpoint(endpoint)
                .requestHash(requestHash)
                .status(IdempotencyKeyStatus.PROCESSING)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();

        try {
            newRecord = idempotencyKeyRepository.saveAndFlush(newRecord);
        } catch (DataIntegrityViolationException e) {
            log.warn("Concurrent idempotency lock caught by DB constraint: key={} userId={}", idempotencyKey, userId);
            throw new ConflictException("A request with this idempotency key is currently being processed.");
        }

        T result;
        try {
            result = action.get();
        } catch (Exception e) {
            log.error("Action execution failed for idempotencyKey={}. Updating status to FAILED.", idempotencyKey, e);
            newRecord.setStatus(IdempotencyKeyStatus.FAILED);
            idempotencyKeyRepository.save(newRecord);
            throw e;
        }

        // Success -> Cache response and update status to COMPLETED
        try {
            String jsonResponseBody = objectMapper.writeValueAsString(result);
            newRecord.setStatus(IdempotencyKeyStatus.COMPLETED);
            newRecord.setResponseStatus(201);
            newRecord.setResponseBody(jsonResponseBody);
            idempotencyKeyRepository.save(newRecord);

            log.info("Idempotency execution completed and stored: key={} userId={} endpoint={}",
                    idempotencyKey, userId, endpoint);

            return IdempotencyResult.<T>builder()
                    .cached(false)
                    .httpStatus(201)
                    .body(result)
                    .build();
        } catch (Exception e) {
            log.error("Failed to serialize idempotency response payload", e);
            return IdempotencyResult.<T>builder()
                    .cached(false)
                    .httpStatus(201)
                    .body(result)
                    .build();
        }
    }

    private String computeHash(Object payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(json.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm unavailable", e);
        } catch (Exception e) {
            return String.valueOf(payload.hashCode());
        }
    }
}
