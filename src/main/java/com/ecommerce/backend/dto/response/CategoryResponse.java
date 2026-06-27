package com.ecommerce.backend.dto.response;

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
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}