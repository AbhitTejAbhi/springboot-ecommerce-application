package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.AddToCartRequest;
import com.ecommerce.backend.dto.request.UpdateCartItemRequest;
import com.ecommerce.backend.dto.response.CartResponse;
import com.ecommerce.backend.dto.response.ApiResponse;
import com.ecommerce.backend.security.CustomUserDetails;
import com.ecommerce.backend.service.CartService;
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
@RestController
@RequestMapping("/api/customer/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AddToCartRequest request) {

        CartResponse response = cartService.addToCart(userDetails.getId(), request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Item added to cart successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        CartResponse response = cartService.getMyCart(userDetails.getId());

        return ResponseEntity
                .ok(ApiResponse.success("Cart fetched successfully", response));
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateQuantity(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {

        CartResponse response = cartService.updateQuantity(
                userDetails.getId(), cartItemId, request);

        return ResponseEntity
                .ok(ApiResponse.success("Cart item updated successfully", response));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long cartItemId) {

        CartResponse response = cartService.removeItem(userDetails.getId(), cartItemId);

        return ResponseEntity
                .ok(ApiResponse.success("Item removed from cart successfully", response));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        cartService.clearCart(userDetails.getId());

        return ResponseEntity
                .ok(ApiResponse.success("Cart cleared successfully"));
    }
}