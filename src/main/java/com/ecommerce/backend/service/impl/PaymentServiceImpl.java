package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.request.CreatePaymentRequest;
import com.ecommerce.backend.dto.request.UpdatePaymentStatusRequest;
import com.ecommerce.backend.dto.response.PaymentResponse;
import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.entity.Payment;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.enums.OrderStatus;
import com.ecommerce.backend.enums.PaymentStatus;
import com.ecommerce.backend.exception.BadRequestException;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.repository.PaymentRepository;
import com.ecommerce.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * All payment business logic lives here. Payment is intentionally
 * kept internal for now — no Razorpay/Stripe integration — so this
 * just models the PENDING -> SUCCESS/FAILED lifecycle against an
 * already-placed Order.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    // ====================================================================
    // Customer operations
    // ====================================================================

    @Override
    @Transactional
    public PaymentResponse createPayment(Long userId, CreatePaymentRequest request) {

        // Order must exist AND belong to the logged-in user — enforced
        // by the repository query itself (findByIdAndUserId), not by a
        // separate authorization check after a plain findById.
        Order order = orderRepository.findByIdAndUserId(request.getOrderId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + request.getOrderId()
                                + " for the logged-in user"));

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException(
                    "Cannot create a payment for a cancelled order");
        }

        if (paymentRepository.existsByOrderId(order.getId())) {
            throw new BadRequestException(
                    "A payment already exists for order id: " + order.getId());
        }

        // Amount always comes from the Order, never from the client —
        // CreatePaymentRequest has no "amount" field at all.
        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .paymentDate(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        log.info("Created payment id={} for orderId={}, userId={}, amount={}",
                savedPayment.getId(), order.getId(), userId, savedPayment.getAmount());

        return mapToResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long userId, Long paymentId) {
        Payment payment = paymentRepository.findByIdAndOrderUserId(paymentId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found with id: " + paymentId));

        return mapToResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getMyPayments(Long userId, Pageable pageable) {
        return paymentRepository.findByOrderUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    // ====================================================================
    // Admin operations
    // ====================================================================

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found with id: " + paymentId));

        return mapToResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse updatePaymentStatus(Long paymentId, UpdatePaymentStatusRequest request) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found with id: " + paymentId));

        PaymentStatus currentStatus = payment.getPaymentStatus();
        PaymentStatus newStatus = request.getPaymentStatus();

        validateTransition(payment, currentStatus, newStatus);

        payment.setPaymentStatus(newStatus);
        Payment updatedPayment = paymentRepository.save(payment);

        // Auto-advance the linked order from PENDING to CONFIRMED the
        // moment payment succeeds, so the order doesn't sit at PENDING
        // after money has actually been collected. Only fires on the
        // PENDING -> CONFIRMED edge specifically — an order already
        // moved past PENDING by other means is left untouched.
        if (newStatus == PaymentStatus.SUCCESS) {
            Order order = payment.getOrder();
            if (order.getOrderStatus() == OrderStatus.PENDING) {
                order.setOrderStatus(OrderStatus.CONFIRMED);
                orderRepository.save(order);
                log.info("Auto-advanced order id={} status PENDING -> CONFIRMED after payment success",
                        order.getId());
            }
        }

        log.info("Updated payment id={} status {} -> {}", paymentId, currentStatus, newStatus);

        return mapToResponse(updatedPayment);
    }

    // ====================================================================
    // Helpers
    // ====================================================================

    /**
     * Enforces the payment status state machine:
     *   PENDING -> SUCCESS
     *   PENDING -> FAILED
     * Both SUCCESS and FAILED are terminal — neither can transition
     * into the other, and re-setting the same terminal status again
     * is also rejected as a no-op edge case. Additionally blocks
     * marking a payment SUCCESS if its order has been cancelled in
     * the meantime, per the explicit business rule.
     */
    private void validateTransition(Payment payment, PaymentStatus current, PaymentStatus next) {

        if (current == PaymentStatus.SUCCESS) {
            throw new BadRequestException("A successful payment cannot be changed to " + next);
        }
        if (current == PaymentStatus.FAILED) {
            throw new BadRequestException("A failed payment cannot be changed to " + next);
        }
        if (current == PaymentStatus.REFUNDED) {
            throw new BadRequestException("A refunded payment cannot be changed to " + next);
        }

        // current == PENDING at this point.
        if (next != PaymentStatus.SUCCESS && next != PaymentStatus.FAILED) {
            throw new BadRequestException(
                    "Invalid payment status transition: " + current + " -> " + next);
        }

        if (next == PaymentStatus.SUCCESS
                && payment.getOrder().getOrderStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException(
                    "Cannot mark payment as SUCCESS — the linked order has been cancelled");
        }
    }

    private PaymentResponse mapToResponse(Payment payment) {
        Order order = payment.getOrder();
        User user = order != null ? order.getUser() : null;

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(order != null ? order.getId() : null)
                .customerName(user != null ? user.getName() : null)
                .customerEmail(user != null ? user.getEmail() : null)
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}