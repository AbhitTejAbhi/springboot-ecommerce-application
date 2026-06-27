package com.ecommerce.backend.dto.request;

import com.ecommerce.backend.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Inbound payload for PATCH /api/admin/payments/{id}/status.
 * Only the target status is accepted — the transition itself is
 * validated server-side (PENDING -> SUCCESS / PENDING -> FAILED only;
 * SUCCESS and FAILED are both terminal).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePaymentStatusRequest {

    @NotNull(message = "Payment status is required")
    private PaymentStatus paymentStatus;
}