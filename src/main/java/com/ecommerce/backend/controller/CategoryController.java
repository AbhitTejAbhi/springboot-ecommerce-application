package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.CategoryRequest;
import com.ecommerce.backend.dto.response.CategoryResponse;
import com.ecommerce.backend.dto.response.ApiResponse;
import com.ecommerce.backend.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Exposes admin-only category management endpoints and public/customer
 * read endpoints.
 *
 * Authorization is enforced by SecurityConfig, not here:
 *   /api/admin/categories/**  -> hasRole("ADMIN")  (covered by /api/admin/**)
 *   /api/categories/**        -> public, per this module's spec
 * If GET /api/categories/** isn't yet explicitly permitted in
 * SecurityConfig, it currently falls through to anyRequest().authenticated() —
 * see the note after this file.
 */
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // ----------------------------------------------------------------
    // Admin APIs — /api/admin/categories/**  (requires hasRole("ADMIN"))
    // ----------------------------------------------------------------

    @PostMapping("/api/admin/categories")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse response = categoryService.createCategory(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", response));
    }

    @PutMapping("/api/admin/categories/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse response = categoryService.updateCategory(id, request);

        return ResponseEntity
                .ok(ApiResponse.success("Category updated successfully", response));
    }

    @DeleteMapping("/api/admin/categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {

        categoryService.deleteCategory(id);

        return ResponseEntity
                .ok(ApiResponse.success("Category deleted successfully"));
    }

    // ----------------------------------------------------------------
    // Public / Customer APIs — /api/categories/**
    // ----------------------------------------------------------------

    @GetMapping("/api/categories")
    public ResponseEntity<ApiResponse<Page<CategoryResponse>>> getAllCategories(
            Pageable pageable) {

        Page<CategoryResponse> response = categoryService.getAllCategories(pageable);

        return ResponseEntity
                .ok(ApiResponse.success("Categories fetched successfully", response));
    }

    @GetMapping("/api/categories/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @PathVariable Long id) {

        CategoryResponse response = categoryService.getCategoryById(id);

        return ResponseEntity
                .ok(ApiResponse.success("Category fetched successfully", response));
    }
}