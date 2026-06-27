package com.ecommerce.backend.dto.request;

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
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name must not exceed 200 characters")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;

    @NotNull(message = "Category id is required")
    private Long categoryId;

    /**
     * Defaults to true at the controller/service boundary if the
     * client omits it (newly created products are active by default);
     * not enforced @NotNull here so admins can create a product
     * without explicitly specifying this every time.
     */
    @Builder.Default
    private boolean active = true;
}