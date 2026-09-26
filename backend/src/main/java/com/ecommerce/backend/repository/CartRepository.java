package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Fetch the single cart belonging to a user. Cart.user is a
     * @OneToOne with a unique FK ("user_id" has unique = true on the
     * join column), so this is an indexed point lookup (idx_cart_user)
     * returning at most one result.
     */
    Optional<Cart> findByUserId(Long userId);
}