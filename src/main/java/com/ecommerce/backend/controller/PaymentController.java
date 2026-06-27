package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.CreatePaymentRequest;
import com.ecommerce.backend.dto.request.UpdatePaymentStatusRequest;
import com.ecommerce.backend.dto.response.PaymentResponse;
import com.ecommerce.backend.dto.response.ApiResponse;
import com.ecommerce.backend.security.CustomUserDetails;
import com.ecommerce.backend.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // ----------------------------------------------------------------
    // Customer APIs — /api/customer/payments/**  (requires hasRole("CUSTOMER"))
    // ----------------------------------------------------------------

    @PostMapping("/api/customer/payments")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreatePaymentRequest request) {

        PaymentResponse response = paymentService.createPayment(userDetails.getId(), request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment created successfully", response));
    }

    @GetMapping("/api/customer/payments")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getMyPayments(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {

        Page<PaymentResponse> response = paymentService.getMyPayments(userDetails.getId(), pageable);

        return ResponseEntity
                .ok(ApiResponse.success("Payments fetched successfully", response));
    }

    @GetMapping("/api/customer/payments/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getMyPayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {

        PaymentResponse response = paymentService.getPayment(userDetails.getId(), id);

        return ResponseEntity
                .ok(ApiResponse.success("Payment fetched successfully", response));
    }

    // ----------------------------------------------------------------
    // Admin APIs — /api/admin/payments/**  (requires hasRole("ADMIN"))
    // ----------------------------------------------------------------

    @GetMapping("/api/admin/payments")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getAllPayments(
            Pageable pageable) {

        Page<PaymentResponse> response = paymentService.getAllPayments(pageable);

        return ResponseEntity
                .ok(ApiResponse.success("Payments fetched successfully", response));
    }

    @GetMapping("/api/admin/payments/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @PathVariable Long id) {

        PaymentResponse response = paymentService.getPaymentById(id);

        return ResponseEntity
                .ok(ApiResponse.success("Payment fetched successfully", response));
    }

    @PatchMapping("/api/admin/payments/{id}/status")
    public ResponseEntity<ApiResponse<PaymentResponse>> updatePaymentStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePaymentStatusRequest request) {

        PaymentResponse response = paymentService.updatePaymentStatus(id, request);

        return ResponseEntity
                .ok(ApiResponse.success("Payment status updated successfully", response));
    }
}