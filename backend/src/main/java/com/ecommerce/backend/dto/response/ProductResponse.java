package com.ecommerce.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(name = "Product Response", description = "Product data returned by the API. Category is flattened to id and name.")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    @Schema(description = "Unique product ID", example = "1")
    private Long id;

    @Schema(description = "Product name", example = "Apple MacBook Pro 14")
    private String name;

    @Schema(description = "Product description", example = "M3 Pro chip, 18GB RAM, 512GB SSD")
    private String description;

    @Schema(description = "Current price", example = "199999.00")
    private BigDecimal price;

    @Schema(description = "Units currently in stock", example = "50")
    private Integer stock;

    @Schema(description = "Cloudinary image URL (null if no image uploaded)", example = "https://res.cloudinary.com/demo/image/upload/v1234/products/laptop.jpg")
    private String imageUrl;

    @Schema(description = "Whether the product is visible to customers", example = "true")
    private boolean active;

    @Schema(description = "ID of the parent category", example = "1")
    private Long categoryId;

    @Schema(description = "Name of the parent category", example = "Electronics")
    private String categoryName;

    @Schema(description = "Timestamp when this product was created", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp of the last update", example = "2024-06-01T14:00:00")
    private LocalDateTime updatedAt;
}