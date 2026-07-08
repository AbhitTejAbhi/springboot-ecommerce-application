package com.ecommerce.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Outbound representation of a single order line item.
 * "priceAtPurchase" and "subtotal" are historical snapshots taken at
 * order-placement time — they deliberately do NOT reflect the
 * product's current price, since an order is a record of what was
 * actually paid, not a live product lookup.
 */

@Schema(name = "Order Item Response", description = "A single line item snapshot within an order. Prices reflect what was actually paid, not the current product price.")
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderItemResponse {

    @Schema(description = "Unique order item ID", example = "5")
    private Long orderItemId;

    @Schema(description = "Product ID", example = "1")
    private Long productId;

    @Schema(description = "Product name at time of purchase", example = "Apple MacBook Pro 14")
    private String productName;

    @Schema(description = "Product image URL", example = "https://res.cloudinary.com/demo/image/upload/v1234/products/laptop.jpg")
    private String productImageUrl;

    @Schema(description = "Quantity ordered", example = "2")
    private Integer quantity;

    @Schema(description = "Unit price at time of purchase (historical snapshot — does not change if the product price changes later)", example = "199999.00")
    private BigDecimal priceAtPurchase;

    @Schema(description = "priceAtPurchase × quantity", example = "399998.00")
    private BigDecimal subtotal;
}