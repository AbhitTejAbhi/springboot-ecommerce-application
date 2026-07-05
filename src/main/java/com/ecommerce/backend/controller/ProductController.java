package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.CreateProductRequest;
import com.ecommerce.backend.dto.request.UpdateProductRequest;
import com.ecommerce.backend.dto.response.ProductResponse;
import com.ecommerce.backend.dto.response.ApiResponse;
import com.ecommerce.backend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 *   /api/products/**        -> authenticated()
 * This controller only implements the business behavior; it doesn't
 * re-check roles itself.
 */
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ----------------------------------------------------------------
    // Admin APIs — /api/admin/products/**  (requires hasRole("ADMIN"))
    // ----------------------------------------------------------------

    @PostMapping("/api/admin/products")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request) {

        ProductResponse response = productService.createProduct(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", response));
    }

    @PutMapping("/api/admin/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {

        ProductResponse response = productService.updateProduct(id, request);

        return ResponseEntity
                .ok(ApiResponse.success("Product updated successfully", response));
    }

    @DeleteMapping("/api/admin/products/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {

        productService.deleteProduct(id);

        return ResponseEntity
                .ok(ApiResponse.success("Product deleted successfully"));
    }

    @PostMapping(value = "/api/admin/products/{id}/image", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<ProductResponse>> uploadProductImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile image) {

        ProductResponse response = productService.uploadProductImage(id, image);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product image uploaded successfully", response));
    }

    @PutMapping(value = "/api/admin/products/{id}/image", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProductImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile image) {

        ProductResponse response = productService.updateProductImage(id, image);

        return ResponseEntity
                .ok(ApiResponse.success("Product image updated successfully", response));
    }

    @DeleteMapping("/api/admin/products/{id}/image")
    public ResponseEntity<ApiResponse<Void>> deleteProductImage(@PathVariable Long id) {

        productService.deleteProductImage(id);

        return ResponseEntity
                .ok(ApiResponse.success("Product image deleted successfully"));
    }

    // ----------------------------------------------------------------
    // Customer APIs — /api/products/**   No Authentication Required
    // ----------------------------------------------------------------

    @GetMapping("/api/products")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAllProducts(
            Pageable pageable) {

        Page<ProductResponse> response = productService.getAllProducts(pageable);

        return ResponseEntity
                .ok(ApiResponse.success("Products fetched successfully", response));
    }

    @GetMapping("/api/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @PathVariable Long id) {

        ProductResponse response = productService.getProductById(id);

        return ResponseEntity
                .ok(ApiResponse.success("Product fetched successfully", response));
    }

    @GetMapping("/api/products/category/{id}")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProductsByCategory(
            @PathVariable Long id,
            Pageable pageable) {

        Page<ProductResponse> response = productService.getProductsByCategory(id, pageable);

        return ResponseEntity
                .ok(ApiResponse.success("Products fetched successfully", response));
    }

    @GetMapping("/api/products/search")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> searchProducts(
            @RequestParam String keyword,
            Pageable pageable) {

        Page<ProductResponse> response = productService.searchProducts(keyword, pageable);

        return ResponseEntity
                .ok(ApiResponse.success("Products fetched successfully", response));
    }
}