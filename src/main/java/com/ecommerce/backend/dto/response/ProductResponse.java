package com.ecommerce.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Outbound representation of a Product. The entity is never returned
 * directly from any controller — every endpoint that exposes product
 * data (admin create/update, and public/customer list/detail/search)
 * maps to this DTO instead.
 *
 * "categoryName" is flattened from the Category association so the
 * client doesn't need a second request just to display it.
 * cartItems/orderItems are deliberately excluded — a product response
 * should never trigger lazy-loading of every cart/order line
 * referencing it.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private boolean active;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}