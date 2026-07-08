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
 * Inbound payload for POST /api/admin/products.
 *
 * "categoryId" references an existing Category by id rather than
 * nesting a full category object — the service layer resolves and
 * validates it against CategoryRepository. No "id", "version",
 * "createdAt"/"updatedAt" — those are server-managed.
 */

@Schema(name = "Create Product Request", description = "Payload for creating a new product in the catalog")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {

    @Schema(description = "Unique product name", example = "Apple MacBook Pro 14", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name must not exceed 200 characters")
    private String name;

    @Schema(description = "Detailed product description", example = "M3 Pro chip, 18GB RAM, 512GB SSD", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Product price (must be greater than 0)", example = "199999.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;

    @Schema(description = "Available stock quantity (0 or more)", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    @Schema(description = "Product image URL (can be updated via /image endpoint)", example = "https://example.com/image.jpg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;

    @Schema(description = "ID of the category this product belongs to", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Category id is required")
    private Long categoryId;

    /**
     * Defaults to true at the controller/service boundary if the
     * client omits it (newly created products are active by default);
     * not enforced @NotNull here so admins can create a product
     * without explicitly specifying this every time.
     */
    @Schema(description = "Whether the product is active and visible to customers", example = "true", defaultValue = "true")
    @Builder.Default
    private boolean active = true;
}