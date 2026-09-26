package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, Long> {

    Optional<IdempotencyKey> findByIdempotencyKeyAndUserIdAndEndpoint(
            String idempotencyKey, Long userId, String endpoint);

    void deleteByExpiresAtBefore(LocalDateTime now);
}
