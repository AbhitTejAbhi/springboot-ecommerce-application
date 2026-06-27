package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.PlaceOrderRequest;
import com.ecommerce.backend.dto.response.OrderResponse;
import com.ecommerce.backend.dto.response.ApiResponse;
import com.ecommerce.backend.enums.OrderStatus;
import com.ecommerce.backend.security.CustomUserDetails;
import com.ecommerce.backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Order endpoints, split into customer-facing (/api/customer/orders/**,
 * ownership-scoped to the authenticated user) and admin-facing
 * (/api/admin/orders/**, unscoped). Authorization for both is enforced
 * by SecurityConfig (hasRole("CUSTOMER") / hasRole("ADMIN")
 * respectively) — this controller only implements the business
 * behavior.
 */
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ----------------------------------------------------------------
    // Customer APIs — /api/customer/orders/**  (requires hasRole("CUSTOMER"))
    // ----------------------------------------------------------------

    @PostMapping("/api/customer/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PlaceOrderRequest request) {

        OrderResponse response = orderService.placeOrder(userDetails.getId(), request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order placed successfully", response));
    }

    @GetMapping("/api/customer/orders")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {

        Page<OrderResponse> response = orderService.getMyOrders(userDetails.getId(), pageable);

        return ResponseEntity
                .ok(ApiResponse.success("Orders fetched successfully", response));
    }

    @GetMapping("/api/customer/orders/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetails(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {

        OrderResponse response = orderService.getOrderDetails(userDetails.getId(), id);

        return ResponseEntity
                .ok(ApiResponse.success("Order details fetched successfully", response));
    }

    @PatchMapping("/api/customer/orders/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {

        OrderResponse response = orderService.cancelOrder(userDetails.getId(), id);

        return ResponseEntity
                .ok(ApiResponse.success("Order cancelled successfully", response));
    }

    // ----------------------------------------------------------------
    // Admin APIs — /api/admin/orders/**  (requires hasRole("ADMIN"))
    // ----------------------------------------------------------------

    @GetMapping("/api/admin/orders")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrders(
            Pageable pageable) {

        Page<OrderResponse> response = orderService.getAllOrders(pageable);

        return ResponseEntity
                .ok(ApiResponse.success("Orders fetched successfully", response));
    }

    @GetMapping("/api/admin/orders/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable Long id) {

        OrderResponse response = orderService.getOrderById(id);

        return ResponseEntity
                .ok(ApiResponse.success("Order fetched successfully", response));
    }

    @PatchMapping("/api/admin/orders/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {

        OrderResponse response = orderService.updateOrderStatus(id, status);

        return ResponseEntity
                .ok(ApiResponse.success("Order status updated successfully", response));
    }
}