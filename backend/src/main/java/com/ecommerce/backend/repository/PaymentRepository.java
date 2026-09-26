package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Lookup a payment by its gateway/PSP transaction id — used for
     * webhook callbacks and reconciliation (e.g. matching an incoming
     * payment-gateway notification back to the local Payment record).
     * Backed by idx_payment_transaction; "transaction_id" is unique,
     * so at most one row can match.
     */
    Optional<Payment> findByTransactionId(String transactionId);

    /**
     * Fetch the single payment tied to a given order. Payment.order is
     * a @OneToOne with a unique FK ("order_id" has unique = true on
     * the join column), so this returns at most one result.
     * Backed by idx_payment_order.
     */
    Optional<Payment> findByOrderId(Long orderId);

    /**
     * Ownership-scoped variant of findByOrderId, traversing
     * Payment -> Order -> User. Not currently used by the service
     * (createPayment already verifies order ownership before this
     * point), but included per spec for any future ownership-scoped
     * lookup by order id.
     */
    Optional<Payment> findByOrderIdAndOrderUserId(Long orderId, Long userId);

    /**
     * Ownership-check lookup: fetch a specific payment only if it
     * belongs to an order owned by the given user, traversing
     * Payment -> Order -> User. This is what prevents one customer
     * from viewing another customer's payment by guessing an id —
     * the repository itself enforces the boundary rather than relying
     * on a separate authorization check after a plain findById.
     */
    Optional<Payment> findByIdAndOrderUserId(Long id, Long userId);

    /**
     * All payments belonging to a given user's orders, paginated,
     * traversing Payment -> Order -> User. Used for "Get My Payments"
     * (payment history).
     */
    Page<Payment> findByOrderUserId(Long userId, Pageable pageable);

    /**
     * Existence check before creating a payment — enforces "an order
     * can have only one payment" at the service layer with a clear
     * error, rather than relying solely on the unique constraint on
     * Payment.order_id to fail the insert.
     */
    boolean existsByOrderId(Long orderId);
}