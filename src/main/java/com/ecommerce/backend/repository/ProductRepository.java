package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // findAll(Pageable) is inherited from JpaRepository — no need to redeclare it.

    /**
     * Products belonging to a given category, paginated.
     * Traverses Product -> Category via the category's "id" field.
     * Backed by idx_product_category for the join column.
     */
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * Storefront listing — only products currently marked active,
     * paginated to avoid loading the entire active catalog at once.
     */
    Page<Product> findByActiveTrue(Pageable pageable);

    /**
     * Case-insensitive partial name search (e.g. search bar),
     * paginated. Backed by idx_product_name for the LIKE scan.
     */
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Existence check before creating a product, to avoid relying
     * solely on a DataIntegrityViolationException for uniqueness
     * validation (Product.name has no unique constraint at the DB
     * level, so this enforces application-level uniqueness if required).
     */
    boolean existsByName(String name);

    /**
     * Used by CategoryServiceImpl before deleting a Category, to give
     * a clear, intentional 400 error ("category still has products")
     * instead of letting the delete fail on the fk_product_category
     * foreign key constraint and surface as an opaque 500.
     */
    boolean existsByCategoryId(Long categoryId);
}