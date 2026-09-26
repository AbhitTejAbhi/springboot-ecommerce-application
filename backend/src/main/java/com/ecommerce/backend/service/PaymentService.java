package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.CreatePaymentRequest;
import com.ecommerce.backend.dto.request.UpdatePaymentStatusRequest;
import com.ecommerce.backend.dto.response.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Payment contract covering customer-facing operations (scoped to a
 * given userId, resolved server-side from the authenticated
 * principal) and admin-facing operations (unscoped — an admin may
 * act on any payment).
 */
public interface PaymentService {

    // ---- Customer operations ----

    PaymentResponse createPayment(Long userId, CreatePaymentRequest request);

    PaymentResponse getPayment(Long userId, Long paymentId);

    Page<PaymentResponse> getMyPayments(Long userId, Pageable pageable);

    // ---- Admin operations ----

    Page<PaymentResponse> getAllPayments(Pageable pageable);

    PaymentResponse getPaymentById(Long paymentId);

    PaymentResponse updatePaymentStatus(Long paymentId, UpdatePaymentStatusRequest request);
}