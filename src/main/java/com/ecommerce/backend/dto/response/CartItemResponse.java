package com.ecommerce.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "Cart Item Response", description = "A single line item in the cart, with product details and computed item total")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    @Schema(description = "Unique cart item ID", example = "3")
    private Long cartItemId;

    @Schema(description = "Product ID", example = "1")
    private Long productId;

    @Schema(description = "Product name", example = "Apple MacBook Pro 14")
    private String productName;

    @Schema(description = "Product image URL", example = "https://res.cloudinary.com/demo/image/upload/v1234/products/laptop.jpg")
    private String productImageUrl;

    @Schema(description = "Current unit price of the product", example = "199999.00")
    private BigDecimal productPrice;

    @Schema(description = "Quantity of this product in the cart", example = "2")
    private Integer quantity;

    @Schema(description = "productPrice × quantity (computed server-side)", example = "399998.00")
    private BigDecimal itemTotal;

    @Schema(description = "Whether the product is active/available", example = "true")
    private Boolean productActive;

    @Schema(description = "Live available stock count for the product", example = "10")
    private Integer productStock;
}