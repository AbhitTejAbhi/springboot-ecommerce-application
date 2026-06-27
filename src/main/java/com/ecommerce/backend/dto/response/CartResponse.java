package com.ecommerce.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Outbound representation of a customer's entire cart — the full
 * line-item list plus the computed grand total. Returned by both
 * GET /api/customer/cart and as the result of any mutating cart
 * operation (add/update/remove), so the client always has the
 * up-to-date full cart state after every action without a separate
 * follow-up GET.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private Long cartId;
    private List<CartItemResponse> items;

    /**
     * Sum of every item's itemTotal — computed server-side, never
     * trusted from the client.
     */
    private BigDecimal grandTotal;

    private Integer totalItems;
}