package com.ecommerce.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Outbound representation of a Category. The entity is never returned
 * directly — "products" (the @OneToMany collection) is deliberately
 * excluded, since a category response should never trigger
 * lazy-loading of every product under it; product listing is handled
 * separately via GET /api/products/category/{id}.
 */
@Schema(name = "Category Response", description = "Category data returned by the API. Products are not nested here — use GET /api/products/category/{id} instead.")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    @Schema(description = "Unique category ID", example = "1")
    private Long id;

    @Schema(description = "Category name", example = "Electronics")
    private String name;

    @Schema(description = "Category description", example = "Gadgets and electronic devices")
    private String description;

    @Schema(description = "Timestamp when this category was created", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp of the last update", example = "2024-06-01T14:00:00")
    private LocalDateTime updatedAt;
}