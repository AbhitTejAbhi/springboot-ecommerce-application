package com.ecommerce.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Inbound payload for PUT /api/customer/cart/{cartItemId}.
 * Only the new quantity is accepted — the cart item itself is
 * identified by the path variable, and its product/cart association
 * is never reassignable through this endpoint.
 */

@Schema(name = "Update Cart Item Request", description = "Payload for setting a cart item's quantity to a new absolute value")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateCartItemRequest {

    @Schema(description = "New absolute quantity for this cart item (must be at least 1). Validated against available stock.", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}