package com.ecommerce.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(
        name = "products",
        indexes = {
                @Index(name = "idx_product_category", columnList = "category_id"),
                @Index(name = "idx_product_name", columnList = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Price must be greater than zero"
    )
    @Column(
            name = "price",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal price;

    @NotNull(message = "Stock is required")
    @Min(
            value = 0,
            message = "Stock cannot be negative"
    )
    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private boolean active = true;

    // Optimistic locking for stock updates
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

    // Many Products -> One Category
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "category_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_category")
    )
    private Category category;

    // One Product -> Many Cart Items
    @Builder.Default
    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY
    )
    private Set<CartItem> cartItems = new LinkedHashSet<>();

    // One Product -> Many Order Items
    @Builder.Default
    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY
    )
    private Set<OrderItem> orderItems = new LinkedHashSet<>();
}

