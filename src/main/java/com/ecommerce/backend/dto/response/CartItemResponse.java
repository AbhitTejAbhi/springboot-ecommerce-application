package com.ecommerce.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Outbound representation of a single cart line item.
 * Flattens the Product association (name, image, price) so the
 * client can render the cart without a second request per item.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private Long cartItemId;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private BigDecimal productPrice;
    private Integer quantity;

    /**
     * productPrice * quantity, computed server-side at response time —
     * never trusted from or sent by the client.
     */
    private BigDecimal itemTotal;
}