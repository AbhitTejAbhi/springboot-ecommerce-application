package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.AddToCartRequest;
import com.ecommerce.backend.dto.request.UpdateCartItemRequest;
import com.ecommerce.backend.dto.response.CartResponse;
import com.ecommerce.backend.dto.response.ApiResponse;
import com.ecommerce.backend.security.CustomUserDetails;
import com.ecommerce.backend.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Customer-only shopping cart endpoints, mapped under
 * /api/customer/cart. Authorization is enforced by SecurityConfig
 * (.requestMatchers("/api/customer/**").hasRole("CUSTOMER")) — this
 * controller only implements the business behavior.
 *
 * The logged-in user's id is resolved from the JWT-authenticated
 * principal via @AuthenticationPrincipal, never accepted as a request
 * parameter — a customer can only ever act on their own cart.
 */

@Tag(name = "5. Cart", description = "Customer shopping cart — add, view, update quantity, remove items, clear cart")
@RestController
@RequestMapping("/api/customer/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Add a product to cart",
            description = "Merges into an existing line item if the product is already in the cart. Validates stock and active status.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Item added to cart successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Product inactive or insufficient stock"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Customer role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Item added to cart successfully",
                        cartService.addToCart(userDetails.getId(), request)));
    }

    @Operation(summary = "View my cart", description = "Returns all line items, individual totals, and grand total.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cart fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Customer role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Cart fetched successfully",
                cartService.getMyCart(userDetails.getId())));
    }

    @Operation(summary = "Update the quantity of a cart item", description = "Validates new quantity against available stock.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cart item updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Insufficient stock"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Customer role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cart item not found or not owned by caller"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateQuantity(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID of the cart item to update", required = true, example = "3")
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cart item updated successfully",
                cartService.updateQuantity(userDetails.getId(), cartItemId, request)));
    }

    @Operation(summary = "Remove a single cart item")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item removed from cart successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Customer role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cart item not found or not owned by caller"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID of the cart item to remove", required = true, example = "3")
            @PathVariable Long cartItemId) {
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart successfully",
                cartService.removeItem(userDetails.getId(), cartItemId)));
    }

    @Operation(summary = "Clear the entire cart", description = "Removes every item from the logged-in customer's cart.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cart cleared successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Customer role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.clearCart(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Cart cleared successfully"));
    }
}