package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.PlaceOrderRequest;
import com.ecommerce.backend.dto.response.OrderResponse;
import com.ecommerce.backend.dto.response.ApiResponse;
import com.ecommerce.backend.enums.OrderStatus;
import com.ecommerce.backend.security.CustomUserDetails;
import com.ecommerce.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

@Tag(name = "6. Order", description = "Order placement, history, cancellation, and admin order management")
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ----------------------------------------------------------------
    // Customer APIs — /api/customer/orders/**  (requires hasRole("CUSTOMER"))
    // ----------------------------------------------------------------

    @Operation(summary = "Place an order from current cart",
            description = "Validates cart contents, address ownership, stock, and active status. " +
                    "Creates a new order from the customer's current cart.\n" +
                    "\n" +
                    "Validates:\n" +
                    "\n" +
                    "• cart contents\n" +
                    "\n" +
                    "• address ownership\n" +
                    "\n" +
                    "• stock availability\n" +
                    "\n" +
                    "• product active status\n" +
                    "\n" +
                    "On success, stock is deducted and the cart is cleared atomically.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Order placed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Empty cart, inactive product, or insufficient stock"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Customer role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found or not owned by caller"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/api/customer/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PlaceOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order placed successfully",
                        orderService.placeOrder(userDetails.getId(), request)));
    }

    @Operation(summary = "View my order history (paginated)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Orders fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Customer role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/api/customer/orders")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Orders fetched successfully",
                orderService.getMyOrders(userDetails.getId(), pageable)));
    }

    @Operation(summary = "Get a single order's details", description = "Only the owner of the order can access it.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order details fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Customer role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found or not owned by caller"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/api/customer/orders/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetails(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID of the order", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Order details fetched successfully",
                orderService.getOrderDetails(userDetails.getId(), id)));
    }

    @Operation(summary = "Cancel an order",
            description = "Allowed from PENDING or CONFIRMED status only. " +
                    "Automatically restocks all ordered products.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Order cannot be cancelled in its current status"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Customer role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found or not owned by caller"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/api/customer/orders/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID of the order to cancel", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully",
                orderService.cancelOrder(userDetails.getId(), id)));
    }

    // ----------------------------------------------------------------
    // Admin APIs — /api/admin/orders/**  (requires hasRole("ADMIN"))
    // ----------------------------------------------------------------
    @Operation(summary = "List all orders (Admin only, paginated)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Orders fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Admin role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/api/admin/orders")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrders(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Orders fetched successfully",
                orderService.getAllOrders(pageable)));
    }

    @Operation(summary = "Get any order by ID (Admin only)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Admin role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/api/admin/orders/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @Parameter(description = "ID of the order", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Order fetched successfully",
                orderService.getOrderById(id)));
    }

    @Operation(summary = "Update an order's status (Admin only)",
            description = "Forward-only pipeline: PENDING →\n" +
                    "CONFIRMED →\n" +
                    "SHIPPED →\n" +
                    "DELIVERED\n" +
                    "\n" +
                    "CANCELLED may be applied\n" +
                    "before reaching a terminal state\n" +
                    "and restores stock.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid status transition"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Admin role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/api/admin/orders/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @Parameter(description = "ID of the order", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "New order status (PENDING/CONFIRMED/SHIPPED/DELIVERED/CANCELLED)",
                    required = true, example = "CONFIRMED")
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully",
                orderService.updateOrderStatus(id, status)));
    }
}