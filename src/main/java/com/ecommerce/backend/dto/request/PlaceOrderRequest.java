package com.ecommerce.backend.dto.request;

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
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderRequest {

    @NotNull(message = "Address id is required")
    private Long addressId;
}