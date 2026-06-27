package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.PlaceOrderRequest;
import com.ecommerce.backend.dto.response.OrderResponse;
import com.ecommerce.backend.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Order contract covering both customer-facing operations (scoped to
 * a given userId, resolved server-side from the authenticated
 * principal) and admin-facing operations (unscoped — an admin may
 * act on any order).
 */
public interface OrderService {

    // ---- Customer operations ----

    OrderResponse placeOrder(Long userId, PlaceOrderRequest request);

    Page<OrderResponse> getMyOrders(Long userId, Pageable pageable);

    OrderResponse getOrderDetails(Long userId, Long orderId);

    OrderResponse cancelOrder(Long userId, Long orderId);

    // ---- Admin operations ----

    Page<OrderResponse> getAllOrders(Pageable pageable);

    OrderResponse getOrderById(Long orderId);

    OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus);
}