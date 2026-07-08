package com.ecommerce.backend.dto.request;

import com.ecommerce.backend.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(name = "Update Payment Status Request",
        description = "Admin-only payload for updating payment status. " +
                "Only PENDING → SUCCESS or PENDING → FAILED transitions are allowed. Both are terminal.")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdatePaymentStatusRequest {

    @Schema(description = "Target payment status",
            example = "SUCCESS",
            allowableValues = {"SUCCESS", "FAILED"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Payment status is required")
    private PaymentStatus paymentStatus;
}