package com.ecommerce.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Inbound payload for POST /api/customer/orders.
 *
 * Only a delivery address reference is required from the client —
 * the order's items, quantities, prices, and total are all derived
 * server-side from the customer's current cart contents at the
 * moment of placement, and must never be accepted directly from the
 * client (a client-supplied price/total would be a critical
 * trust-boundary violation).
 */

@Schema(name = "Place Order Request",
        description = "Payload for placing an order from the current cart. " +
                "Total amount, order items, and payment are all derived server-side — " +
                "only the delivery address is required from the client.")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderRequest {

    @Schema(description = "ID of the delivery address (must belong to the logged-in user)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Address id is required")
    private Long addressId;
}