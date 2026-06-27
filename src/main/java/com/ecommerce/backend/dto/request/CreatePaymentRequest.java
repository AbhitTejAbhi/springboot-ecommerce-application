package com.ecommerce.backend.dto.request;

import com.ecommerce.backend.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Inbound payload for POST /api/customer/payments.
 *
 * Only the order reference and chosen method are accepted from the
 * client — "amount" is deliberately absent: it is always derived
 * server-side from Order.totalAmount, never trusted from the client,
 * per the explicit business rule. "paymentStatus" is also absent —
 * every new payment starts at PENDING regardless of input.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {

    @NotNull(message = "Order id is required")
    private Long orderId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}