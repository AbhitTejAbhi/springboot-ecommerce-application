package com.ecommerce.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Inbound payload for both POST /api/admin/categories (create) and
 * PUT /api/admin/categories/{id} (update). A single request DTO is
 * sufficient here — unlike Product, Category has no fields (like
 * "active") that differ in meaning/requirement between create and
 * update, so a separate CreateCategoryRequest/UpdateCategoryRequest
 * split would just duplicate the same two fields.
 *
 * No "id", "createdAt"/"updatedAt" — those are server-managed.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    private String description;
}