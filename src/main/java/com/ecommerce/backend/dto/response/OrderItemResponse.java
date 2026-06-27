package com.ecommerce.backend.dto.response;

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
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private Long orderItemId;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
    private BigDecimal subtotal;
}