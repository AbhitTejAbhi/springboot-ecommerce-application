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
 * Inbound payload for POST /api/customer/cart.
 * The cart itself is resolved server-side from the authenticated
 * user (via @AuthenticationPrincipal) — never accepted from the client.
 */

@Schema(name = "Add To Cart Request", description = "Payload for adding a product to the cart or merging into an existing cart line item")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AddToCartRequest {

    @Schema(description = "ID of the product to add", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Product id is required")
    private Long productId;

    @Schema(description = "Quantity to add (minimum 1). If the product is already in the cart, this is added to the existing quantity.", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}