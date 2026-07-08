package com.ecommerce.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Inbound payload for PUT /api/admin/products/{id}.
 *
 * Same validation rules as CreateProductRequest. "active" is included
 * here (unlike create, where it defaults true) since updating a
 * product's active/inactive status is itself a primary use case for
 * this endpoint (e.g. discontinuing a product without deleting it).
 */

@Schema(name = "Update Product Request", description = "Payload for updating an existing product. All fields are required.")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {

    @Schema(description = "Updated product name (must remain unique)", example = "Apple MacBook Pro 16", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name must not exceed 200 characters")
    private String name;

    @Schema(description = "Updated product description", example = "M3 Max chip, 36GB RAM, 1TB SSD")
    private String description;

    @Schema(description = "Updated price (must be greater than 0)", example = "249999.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;

    @Schema(description = "Updated stock quantity", example = "30", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    @Schema(description = "Product image URL", example = "https://example.com/new-image.jpg")
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;

    @Schema(description = "Set to false to deactivate/hide the product from customers", example = "true")
    private boolean active;

    @Schema(description = "ID of the updated category", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Category id is required")
    private Long categoryId;
}
