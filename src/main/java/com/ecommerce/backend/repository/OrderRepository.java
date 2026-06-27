package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.enums.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    /**
     * Order history for a given user, paginated (e.g. "My Orders" screen).
     * Backed by idx_order_user. Never return an unbounded order list —
     * a long-time customer could have hundreds of historical orders.
     */
    Page<Order> findByUserId(Long userId, Pageable pageable);

    /**
     * Filter orders by status, paginated — used for admin/fulfillment
     * dashboards (e.g. "show all PENDING orders to process").
     * Backed by idx_order_status.
     */
    Page<Order> findByOrderStatus(OrderStatus orderStatus, Pageable pageable);
    /**
     * Ownership-check lookup: fetch a specific order only if it
     * belongs to the given user. This is what prevents one customer
     * from viewing or cancelling another customer's order by guessing
     * an id — the repository itself enforces the boundary rather than
     * relying on a separate authorization check after a plain findById.
     * Used by getOrderDetails() and cancelOrder() (customer-facing,
     * ownership-scoped); admin endpoints use plain findById instead,
     * since an admin may legitimately access any order.
     */
    Optional<Order> findByIdAndUserId(Long id, Long userId);
}
