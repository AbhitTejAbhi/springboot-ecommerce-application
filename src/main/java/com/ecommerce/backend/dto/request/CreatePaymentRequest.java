package com.ecommerce.backend.dto.request;

import com.ecommerce.backend.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(name = "Create Payment Request",
        description = "Payload for initiating a payment against a placed order. " +
                "Amount is always taken from the order total — never supplied by the client.")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {

    @Schema(description = "ID of the order to pay for (must belong to the logged-in user)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Order id is required")
    private Long orderId;

    @Schema(description = "Chosen payment method", example = "UPI",
            allowableValues = {"COD", "UPI", "CARD", "NET_BANKING"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}