package com.ecommerce.backend.dto.response;

import com.ecommerce.backend.enums.PaymentMethod;
import com.ecommerce.backend.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Outbound representation of a Payment.
 * "customerName"/"customerEmail" are flattened from
 * Payment -> Order -> User (the same pattern used in OrderResponse)
 * rather than a bare userId, so an admin payment list is immediately
 * readable without a follow-up user lookup.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long paymentId;
    private Long orderId;
    private String customerName;
    private String customerEmail;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}