package com.ecommerce.backend.dto.response;

import com.ecommerce.backend.enums.PaymentMethod;
import com.ecommerce.backend.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "Payment Response", description = "Payment record details — customer identity is flattened from the linked order")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    @Schema(description = "Unique payment ID", example = "1")
    private Long paymentId;

    @Schema(description = "ID of the order this payment is for", example = "1")
    private Long orderId;

    @Schema(description = "Name of the customer who owns the linked order", example = "John Doe")
    private String customerName;

    @Schema(description = "Email of the customer who owns the linked order", example = "john@example.com")
    private String customerEmail;

    @Schema(description = "Payment amount (derived from order total — never client-supplied)", example = "399998.00")
    private BigDecimal amount;

    @Schema(description = "Payment method selected by the customer",
            example = "UPI",
            allowableValues = {"COD", "UPI", "CARD", "NET_BANKING"})
    private PaymentMethod paymentMethod;

    @Schema(description = "Current payment status",
            example = "SUCCESS",
            allowableValues = {"PENDING", "SUCCESS", "FAILED", "REFUNDED"})
    private PaymentStatus paymentStatus;

    @Schema(description = "Timestamp when the payment record was created", example = "2024-06-01T14:05:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp of the last status update", example = "2024-06-01T14:10:00")
    private LocalDateTime updatedAt;
}