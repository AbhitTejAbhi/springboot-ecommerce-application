package com.ecommerce.backend.entity;


import com.ecommerce.backend.enums.PaymentMethod;
import com.ecommerce.backend.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(
                        name = "idx_payment_transaction",
                        columnList = "transaction_id"
                ),
                @Index(
                        name = "idx_payment_status",
                        columnList = "payment_status"
                ),
                @Index(
                        name = "idx_payment_order",
                        columnList = "order_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Payment method is required")
    @Enumerated(EnumType.STRING)
    @Column(
            name = "payment_method",
            nullable = false,
            length = 30
    )
    private PaymentMethod paymentMethod;

    @Column(
            name = "transaction_id",
            unique = true,
            length = 200
    )
    private String transactionId;

    @NotNull(message = "Amount is required")
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Amount must be greater than zero"
    )
    @Column(
            name = "amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal amount;

    @NotNull(message = "Payment status is required")
    @Enumerated(EnumType.STRING)
    @Column(
            name = "payment_status",
            nullable = false,
            length = 30
    )
    private PaymentStatus paymentStatus;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    // Optimistic locking for payment state transitions
    @Version
    @Column(name = "version")
    private Long version;

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    // One Payment -> One Order (owning side)
    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "order_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_payment_order")
    )
    private Order order;
}
