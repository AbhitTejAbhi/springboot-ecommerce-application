package com.ecommerce.backend.dto.response;

import com.ecommerce.backend.enums.OrderStatus;
import com.ecommerce.backend.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(name = "Order Response", description = "Full order details including line items, shipping address, and payment status")
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderResponse {

    @Schema(description = "Unique order ID", example = "1")
    private Long orderId;

    @Schema(description = "Name of the customer who placed the order", example = "John Doe")
    private String customerName;

    @Schema(description = "Email of the customer who placed the order", example = "john@example.com")
    private String customerEmail;

    @Schema(description = "Total order amount computed at order placement time", example = "399998.00")
    private BigDecimal totalAmount;

    @Schema(description = "Current order status",
            example = "PENDING",
            allowableValues = {"PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"})
    private OrderStatus orderStatus;

    @Schema(description = "Current payment status — null if no payment has been initiated yet",
            example = "SUCCESS",
            allowableValues = {"PENDING", "SUCCESS", "FAILED", "REFUNDED"},
            nullable = true)
    private PaymentStatus paymentStatus;

    @Schema(description = "ID of the delivery address", example = "1")
    private Long addressId;

    @Schema(description = "Full formatted shipping address", example = "12B, MG Road, Bengaluru, Karnataka, India, 560001")
    private String shippingAddress;

    @Schema(description = "All line items that make up this order")
    private List<OrderItemResponse> orderItems;

    @Schema(description = "Timestamp when the order was placed", example = "2024-06-01T14:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp of the last order update", example = "2024-06-02T10:30:00")
    private LocalDateTime updatedAt;
}