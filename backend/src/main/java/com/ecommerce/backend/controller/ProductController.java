package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.CreateProductRequest;
import com.ecommerce.backend.dto.request.UpdateProductRequest;
import com.ecommerce.backend.dto.response.ProductResponse;
import com.ecommerce.backend.dto.response.ApiResponse;
import com.ecommerce.backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Exposes both admin-only catalog management endpoints and
 * customer-facing read endpoints.
 *
 * Authorization is enforced by SecurityConfig, not here:
 *   /api/admin/products/**  -> hasRole("ADMIN")
 *   /api/products/**        -> permitAll()
 * This controller only implements the business behavior; it doesn't
 * re-check roles itself.
 */
@Tag(name = "3. Product", description = "Admin product/image management and customer product browsing")
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ----------------------------------------------------------------
    // Admin APIs — /api/admin/products/**  (requires hasRole("ADMIN"))
    // ----------------------------------------------------------------
    @Operation(summary = "Create a product", description = "Requires ADMIN role. Creates a new product under an existing category. Product names must be unique.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Product created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed, duplicate name, or category not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Admin role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/api/admin/products")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully",
                        productService.createProduct(request)));
    }

    @Operation(summary = "Update a product", description = "Admin only.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Admin role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product or category not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/api/admin/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @Parameter(description = "ID of the product to update", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully",
                productService.updateProduct(id, request)));
    }

    @Operation(summary = "Delete a product", description = "Admin only. Hard delete.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Admin role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/api/admin/products/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @Parameter(description = "ID of the product to delete", required = true, example = "1")
            @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
    }

    //---------------Admin:Image----------

    @Operation(summary = "Upload a product image", description = "Admin only. Max 5MB. JPEG/PNG/JPG/WEBP only. Always overwrites any existing image.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Product image uploaded successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid file type, empty file, or file exceeds 5MB"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Admin role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/api/admin/products/{id}/image", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<ProductResponse>> uploadProductImage(
            @Parameter(description = "ID of the product", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Product image file (JPEG/PNG/JPG/WEBP, max 5MB)", required = true)
            @RequestParam("image") MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product image uploaded successfully",
                        productService.uploadProductImage(id, image)));
    }

    @Operation(summary = "Replace a product image", description = "Admin only. Deletes existing Cloudinary image and uploads new one.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product image updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid file type, empty file, or file exceeds 5MB"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Admin role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(value = "/api/admin/products/{id}/image", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProductImage(
            @Parameter(description = "ID of the product", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Replacement image file (JPEG/PNG/JPG/WEBP, max 5MB)", required = true)
            @RequestParam("image") MultipartFile image) {
        return ResponseEntity.ok(ApiResponse.success("Product image updated successfully",
                productService.updateProductImage(id, image)));
    }

    @Operation(summary = "Delete a product's image", description = "Admin only. Removes from Cloudinary and sets imageUrl to null.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product image deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Admin role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/api/admin/products/{id}/image")
    public ResponseEntity<ApiResponse<Void>> deleteProductImage(
            @Parameter(description = "ID of the product", required = true, example = "1")
            @PathVariable Long id) {
        productService.deleteProductImage(id);
        return ResponseEntity.ok(ApiResponse.success("Product image deleted successfully"));
    }

    // ----------------------------------------------------------------
    // Customer APIs — /api/products/**   No Authentication Required
    // ----------------------------------------------------------------

    @Operation(summary = "List all products (paginated)", description = "Publicly accessible — no authentication required.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Products fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirements
    @GetMapping("/api/products")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAllProducts(
            @ParameterObject
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Products fetched successfully",
                productService.getAllProducts(pageable)));
    }

    @Operation(summary = "Get a product by ID", description = "Publicly accessible — retrieves a single product by its unique identifier.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirements
    @GetMapping("/api/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @Parameter(description = "ID of the product", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Product fetched successfully",
                productService.getProductById(id)));
    }

    @Operation(summary = "List products by category (paginated)", description = "Publicly accessible — no authentication required.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Products fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirements
    @GetMapping("/api/products/category/{id}")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProductsByCategory(
            @Parameter(description = "ID of the category", required = true, example = "1")
            @PathVariable Long id,
            @ParameterObject
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Products fetched successfully",
                productService.getProductsByCategory(id, pageable)));
    }

    @Operation(summary = "Search products by keyword (paginated)", description = "Case-insensitive partial name match. publically accissible - no authentication required.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Products fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirements
    @GetMapping("/api/products/search")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> searchProducts(
            @Parameter(description = "Search keyword (partial product name)", required = true, example = "laptop")
            @RequestParam String keyword,
            @ParameterObject
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Products fetched successfully",
                productService.searchProducts(keyword, pageable)));
    }
}