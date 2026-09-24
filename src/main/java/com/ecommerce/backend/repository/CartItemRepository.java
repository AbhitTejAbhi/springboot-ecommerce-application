package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Lookup a specific product's line item within a specific cart.
     * Backed by the composite unique constraint uq_cart_product
     * (cart_id, product_id), so at most one row can ever match —
     * used to check "is this product already in the cart" before
     * deciding whether to increment quantity or insert a new row.
     */
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    /**
     * All line items for a given cart (e.g. rendering the cart page).
     * Backed by idx_cart_item_cart.
     */
    List<CartItem> findByCartId(Long cartId);

    /**
     * All cart items belonging to a given user, traversing
     * CartItem -> Cart -> User. CartItem has no direct "userId" field
     * (it belongs to exactly one Cart, which belongs to exactly one
     * User), so this uses the nested property path "cart.user.id".
     * Used by getMyCart() to render the full cart in one query.
     */
    List<CartItem> findByCartUserId(Long userId);

    /**
     * Lookup a specific product's line item within a specific user's
     * cart, traversing CartItem -> Cart -> User. Used by addToCart()
     * to decide whether to increment an existing line item's quantity
     * or insert a new one — the duplicate-merge logic the task spec
     * calls for.
     */
    Optional<CartItem> findByCartUserIdAndProductId(Long userId, Long productId);

    /**
     * Ownership-check lookup: fetch a specific cart item only if it
     * belongs to the given user. This is what prevents one customer
     * from updating/deleting another customer's cart item by guessing
     * an id — the repository itself enforces the boundary rather than
     * relying on a separate authorization check after a plain findById.
     */
    Optional<CartItem> findByIdAndCartUserId(Long id, Long userId);

    /**
     * Bulk-deletes every cart item belonging to a user's cart in a
     * single statement (clearCart()), rather than loading every item
     * into memory just to delete them one by one.
     *
     * @Modifying is required for any DML query; @Transactional at the
     * service layer (not declared here) governs the actual transaction
     * boundary this runs within.
     */
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.user.id = :userId")
    void deleteByCartUserId(@Param("userId") Long userId);

    /**
     * Check if any customer cart currently contains this product.
     */
    boolean existsByProductId(Long productId);
}