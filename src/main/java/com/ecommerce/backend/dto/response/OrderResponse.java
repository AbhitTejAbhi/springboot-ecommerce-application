package com.ecommerce.backend.dto.response;

import com.ecommerce.backend.enums.OrderStatus;
import com.ecommerce.backend.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Outbound representation of a full Order — header fields plus the
 * nested line items, returned by place/get/cancel/admin-update flows
 * alike so the client never needs a second call just to see what was
 * ordered.
 *
 * "userId" is intentionally omitted: on customer endpoints the
 * logged-in user already knows they're viewing their own orders, and
 * on admin endpoints a bare numeric id isn't useful on its own —
 * customerName/customerEmail are flattened in instead so the admin
 * order list is immediately readable without a follow-up user lookup.
 *
 * "paymentStatus" is flattened from the Order -> Payment association
 * so the frontend can show order status and payment status together
 * (e.g. "CONFIRMED" / "PAID") without a second API call. It is
 * nullable: an order may not yet have an associated Payment row if
 * payment hasn't been initiated/recorded for it.
 *
 * The shipping address is flattened to a single display-ready string
 * rather than nesting a full AddressResponse, since order history
 * only needs to show where it was shipped, not allow editing it from
 * this view.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long orderId;
    private String customerName;
    private String customerEmail;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private Long addressId;
    private String shippingAddress;
    private List<OrderItemResponse> orderItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}