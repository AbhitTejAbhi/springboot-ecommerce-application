package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.CategoryRequest;
import com.ecommerce.backend.dto.response.CategoryResponse;
import com.ecommerce.backend.dto.response.ApiResponse;
import com.ecommerce.backend.service.CategoryService;
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
@Tag(name = "2. Category", description = "Admin category management and public category browsing")
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // ----------------------------------------------------------------
    // Admin APIs — /api/admin/categories/**  (requires hasRole("ADMIN"))
    // ----------------------------------------------------------------
    @Operation(summary = "Create a category", description = "Admin only. Category name must be unique.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Category created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed or name already exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Admin role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/api/admin/categories")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully",
                        categoryService.createCategory(request)));
    }

    @Operation(summary = "Update a category", description = "Admin only. Name must remain unique.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed or name already exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Admin role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/api/admin/categories/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @Parameter(description = "ID of the category to update", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully",
                categoryService.updateCategory(id, request)));
    }

    @Operation(summary = "Delete a category",
            description = "Admin only. Fails if products are still assigned to this category.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Category still has products assigned"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Admin role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/api/admin/categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @Parameter(description = "ID of the category to delete", required = true, example = "1")
            @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully"));
    }

    // ----------------------------------------------------------------
    // Public / Customer APIs — /api/categories/**
    // ----------------------------------------------------------------

    @Operation(summary = "List all categories (paginated)", description = "Publicly accessible — no token required.")
    @SecurityRequirements
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categories fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/api/categories")
    public ResponseEntity<ApiResponse<Page<CategoryResponse>>> getAllCategories(
            @ParameterObject
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Categories fetched successfully",
                categoryService.getAllCategories(pageable)));
    }

    @Operation(summary = "Get a category by ID", description = "Publicly accessible — no token required.")
    @SecurityRequirements
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/api/categories/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @Parameter(description = "ID of the category", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Category fetched successfully",
                categoryService.getCategoryById(id)));
    }
}