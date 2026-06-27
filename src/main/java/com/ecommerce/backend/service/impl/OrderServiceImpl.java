package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.request.PlaceOrderRequest;
import com.ecommerce.backend.dto.response.OrderItemResponse;
import com.ecommerce.backend.dto.response.OrderResponse;
import com.ecommerce.backend.entity.*;
import com.ecommerce.backend.enums.OrderStatus;
import com.ecommerce.backend.enums.PaymentStatus;
import com.ecommerce.backend.exception.BadRequestException;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.*;
import com.ecommerce.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * All order business logic lives here — the most transaction-sensitive
 * service in the application, since placeOrder() and cancelOrder()
 * mutate Product stock, Order/OrderItem records, and the Cart all in
 * one atomic unit of work.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    /**
     * Statuses from which a customer-initiated cancel is allowed.
     * SHIPPED and DELIVERED are deliberately excluded — once an order
     * has left the warehouse, cancellation must go through a real
     * returns/refund process, not a simple status flip.
     */
    private static final Set<OrderStatus> CANCELLABLE_STATUSES =
            Set.of(OrderStatus.PENDING, OrderStatus.CONFIRMED);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;

    // ====================================================================
    // Customer operations
    // ====================================================================

    @Override
    @Transactional
    public OrderResponse placeOrder(Long userId, PlaceOrderRequest request) {

        User user = getUserOrThrow(userId);

        // Shipping address must belong to the logged-in user — enforced
        // by the repository query itself (findByIdAndUserId), not by a
        // separate authorization check after a plain findById.
        Address address = addressRepository.findByIdAndUserId(request.getAddressId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found with id: " + request.getAddressId()
                                + " for the logged-in user"));

        List<CartItem> cartItems = cartItemRepository.findByCartUserId(userId);

        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cannot place an order with an empty cart");
        }

        // Validate every line item BEFORE mutating anything — every
        // product must still exist, be active, and have sufficient
        // stock for the requested quantity. Failing fast here, before
        // any Order/OrderItem is created or any stock is touched,
        // keeps the operation atomic in spirit even before the
        // @Transactional rollback would catch a later failure.
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            if (!product.isActive()) {
                throw new BadRequestException(
                        "Product is no longer available: " + product.getName());
            }
            if (product.getStock() < cartItem.getQuantity()) {
                throw new BadRequestException(
                        "Insufficient stock for product: " + product.getName()
                                + ". Available: " + product.getStock()
                                + ", requested: " + cartItem.getQuantity());
            }
        }

        // Total is computed server-side from each item's live price at
        // the moment of purchase — never trusted from the client.
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            BigDecimal lineTotal = cartItem.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(lineTotal);
        }

        Order order = Order.builder()
                .user(user)
                .address(address)
                .totalAmount(totalAmount)
                .orderStatus(OrderStatus.PENDING)
                .build();
        Order savedOrder = orderRepository.save(order);

        // Create OrderItems as historical snapshots (priceAtPurchase,
        // subtotal) and reduce stock for each — both happen together,
        // per cart item, so the two never drift apart within this loop.
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            BigDecimal priceAtPurchase = product.getPrice();
            BigDecimal subtotal = priceAtPurchase.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(priceAtPurchase)
                    .subtotal(subtotal)
                    .build();
            orderItemRepository.save(orderItem);

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        // Cart is cleared only after the order and every OrderItem/stock
        // deduction has succeeded — if anything above threw, this line
        // never runs and @Transactional rolls back everything, so the
        // customer's cart is never lost on a failed order attempt.
        cartItemRepository.deleteByCartUserId(userId);

        log.info("Placed order id={} for userId={} with totalAmount={}",
                savedOrder.getId(), userId, totalAmount);

        return buildOrderResponse(savedOrder);
    }

    @Override
    public Page<OrderResponse> getMyOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(this::buildOrderResponse);
    }

    @Override
    public OrderResponse getOrderDetails(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId));

        return buildOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId));

        return performCancel(order);
    }

    // ====================================================================
    // Admin operations
    // ====================================================================

    @Override
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::buildOrderResponse);
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId));

        return buildOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId));

        OrderStatus currentStatus = order.getOrderStatus();

        if (currentStatus == OrderStatus.CANCELLED) {
            throw new BadRequestException("Order is already cancelled");
        }
        if (currentStatus == OrderStatus.DELIVERED) {
            throw new BadRequestException("Cannot change status of a delivered order");
        }

        // Cancelling via the admin status-update endpoint reuses the
        // exact same restock logic as customer-initiated cancellation,
        // so stock is restored consistently regardless of which path
        // triggered the cancellation.
        if (newStatus == OrderStatus.CANCELLED) {
            return performCancel(order);
        }

        validateForwardTransition(currentStatus, newStatus);

        order.setOrderStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        log.info("Admin updated order id={} status {} -> {}", orderId, currentStatus, newStatus);

        return buildOrderResponse(updatedOrder);
    }

    // ====================================================================
    // Helpers
    // ====================================================================

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));
    }

    /**
     * Shared cancel implementation for both the customer-facing
     * PATCH /api/customer/orders/{id}/cancel endpoint and the
     * admin status-update endpoint when newStatus == CANCELLED.
     * Restocks every ordered product's quantity back onto Product.stock
     * before flipping the order to CANCELLED.
     */
    private OrderResponse performCancel(Order order) {
        OrderStatus currentStatus = order.getOrderStatus();

        if (currentStatus == OrderStatus.CANCELLED) {
            throw new BadRequestException("Order is already cancelled");
        }
        if (!CANCELLABLE_STATUSES.contains(currentStatus)) {
            throw new BadRequestException(
                    "Order cannot be cancelled once it is " + currentStatus);
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);

        log.info("Cancelled order id={}, restocked {} product line(s)",
                order.getId(), orderItems.size());

        return buildOrderResponse(cancelledOrder);
    }

    /**
     * Enforces the forward-only status pipeline:
     *   PENDING -> CONFIRMED -> SHIPPED -> DELIVERED
     * Skipping stages (e.g. PENDING straight to DELIVERED) or moving
     * backward is rejected — CANCELLED is handled separately above
     * and is reachable from any non-terminal state.
     */
    private void validateForwardTransition(OrderStatus current, OrderStatus next) {
        boolean valid =
                (current == OrderStatus.PENDING && next == OrderStatus.CONFIRMED) ||
                        (current == OrderStatus.CONFIRMED && next == OrderStatus.SHIPPED) ||
                        (current == OrderStatus.SHIPPED && next == OrderStatus.DELIVERED);

        if (!valid) {
            throw new BadRequestException(
                    "Invalid order status transition: " + current + " -> " + next);
        }
    }

    private OrderResponse buildOrderResponse(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());

        List<OrderItemResponse> itemResponses = items.stream()
                .map(this::mapToItemResponse)
                .toList();

        Address address = order.getAddress();
        String shippingAddress = formatAddress(address);

        User user = order.getUser();

        // Payment is the inverse side of a @OneToOne and may not exist
        // yet (e.g. order placed but payment not yet initiated/recorded),
        // so this is deliberately nullable rather than throwing.
        Payment payment = order.getPayment();
        PaymentStatus paymentStatus = payment != null ? payment.getPaymentStatus() : null;

        return OrderResponse.builder()
                .orderId(order.getId())
                .customerName(user != null ? user.getName() : null)
                .customerEmail(user != null ? user.getEmail() : null)
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(paymentStatus)
                .addressId(address != null ? address.getId() : null)
                .shippingAddress(shippingAddress)
                .orderItems(itemResponses)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private OrderItemResponse mapToItemResponse(OrderItem item) {
        Product product = item.getProduct();

        return OrderItemResponse.builder()
                .orderItemId(item.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImageUrl(product.getImageUrl())
                .quantity(item.getQuantity())
                .priceAtPurchase(item.getPriceAtPurchase())
                .subtotal(item.getSubtotal())
                .build();
    }

    private String formatAddress(Address address) {
        if (address == null) {
            return null;
        }
        return String.join(", ",
                address.getHouseNumber(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getCountry(),
                address.getPincode());
    }
}