package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.CreatePaymentRequest;
import com.ecommerce.backend.dto.request.UpdatePaymentStatusRequest;
import com.ecommerce.backend.dto.response.PaymentResponse;
import com.ecommerce.backend.dto.response.ApiResponse;
import com.ecommerce.backend.security.CustomUserDetails;
import com.ecommerce.backend.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Payment endpoints, split into customer-facing (/api/customer/payments/**,
 * ownership-scoped to the authenticated user) and admin-facing
 * (/api/admin/payments/**, unscoped). Authorization for both is
 * enforced by SecurityConfig (hasRole("CUSTOMER") / hasRole("ADMIN")
 * respectively) — this controller only implements the business
 * behavior.
 *
 * No payment gateway (Razorpay/Stripe) is integrated yet — this
 * models the internal PENDING -> SUCCESS/FAILED lifecycle only.
 */

@Tag(name = "7. Payment", description = "Customer payment creation/history and admin payment status management")
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // ----------------------------------------------------------------
    // Customer APIs — /api/customer/payments/**  (requires hasRole("CUSTOMER"))
    // ----------------------------------------------------------------

    @Operation(summary = "Create a payment for an order",
            description = "Amount is always derived from the order total — never accepted from the client. " +
                    "Creates a payment in the  PENDING status.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Payment created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Order is cancelled or a payment already exists for it"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Customer role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found or not owned by caller"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/api/customer/payments")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreatePaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment created successfully",
                        paymentService.createPayment(userDetails.getId(), request)));
    }

    @Operation(summary = "view my payment history (paginated)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payments fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Customer role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/api/customer/payments")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getMyPayments(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Payments fetched successfully",
                paymentService.getMyPayments(userDetails.getId(), pageable)));
    }

    @Operation(summary = "Get a single payment by ID", description = "Only the owner of the linked order can access it.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Customer role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found or not owned by caller"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/api/customer/payments/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getMyPayment(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID of the payment", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Payment fetched successfully",
                paymentService.getPayment(userDetails.getId(), id)));
    }

    // ----------------------------------------------------------------
    // Admin APIs — /api/admin/payments/**  (requires hasRole("ADMIN"))
    // ----------------------------------------------------------------

    @Operation(summary = "List all payments (Admin only, paginated)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payments fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Admin role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/api/admin/payments")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getAllPayments(
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Payments fetched successfully",
                paymentService.getAllPayments(pageable)));
    }

    @Operation(summary = "Get any payment by ID (Admin only)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Admin role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/api/admin/payments/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @Parameter(description = "ID of the payment", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Payment fetched successfully",
                paymentService.getPaymentById(id)));
    }

    @Operation(summary = "Update payment status (Admin only)",
            description = "PENDING → SUCCESS or PENDING → FAILED only. Both are terminal. " +
                    "Marking SUCCESS auto-advances a PENDING order to CONFIRMED.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment status updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid status transition or linked order is cancelled"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Admin role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Payment not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/api/admin/payments/{id}/status")
    public ResponseEntity<ApiResponse<PaymentResponse>> updatePaymentStatus(
            @Parameter(description = "ID of the payment", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdatePaymentStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Payment status updated successfully",
                paymentService.updatePaymentStatus(id, request)));
    }
}