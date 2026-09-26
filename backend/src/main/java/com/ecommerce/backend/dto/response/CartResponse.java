package com.ecommerce.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(name = "Cart Response", description = "Full cart state — all line items plus computed totals. Returned after every cart mutation so the client always has fresh state.")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    @Schema(description = "Unique cart ID", example = "1")
    private Long cartId;

    @Schema(description = "All line items in the cart")
    private List<CartItemResponse> items;

    /**
     * Sum of every item's itemTotal — computed server-side, never
     * trusted from the client.
     */
    @Schema(description = "Sum of all item totals (computed server-side)", example = "399998.00")
    private BigDecimal grandTotal;

    @Schema(description = "Total number of distinct line items in the cart", example = "2")
    private Integer totalItems;
}