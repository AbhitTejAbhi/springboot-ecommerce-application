package com.ecommerce.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(
        name = "carts",
        indexes = {
                @Index(name = "idx_cart_user", columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    // One Cart -> One User
    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_cart_user")
    )
    private User user;

    // One Cart -> Many CartItems
    @Builder.Default
    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private Set<CartItem> cartItems = new LinkedHashSet<>();

    public void addCartItem(CartItem cartItem) {
        if (cartItem != null) {
            cartItems.add(cartItem);
            cartItem.setCart(this);
        }
    }

    public void removeCartItem(CartItem cartItem) {
        if (cartItem != null) {
            cartItems.remove(cartItem);
            cartItem.setCart(null);
        }
    }
}
