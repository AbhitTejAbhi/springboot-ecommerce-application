package com.ecommerce.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(name = "Category Request", description = "Payload for creating or updating a category")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @Schema(description = "Unique category name", example = "Electronics", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    @Schema(description = "Optional description of the category", example = "Gadgets and electronic devices", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;
}