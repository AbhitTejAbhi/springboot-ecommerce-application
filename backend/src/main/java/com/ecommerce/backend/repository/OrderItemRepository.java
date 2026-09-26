package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {

    /**
     * All line items belonging to a given order (e.g. rendering an
     * order detail / invoice view). Backed by idx_order_item_order.
     * A List is appropriate here (not Page) since an order's item
     * count is naturally bounded by what a customer can add to a cart.
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * Check if any completed order contains this product.
     */
    boolean existsByProductId(Long productId);
}
