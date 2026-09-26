package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    /**
     * Lookup by category name — "name" has a unique constraint
     * + DB index (idx_category_name).
     */
    Optional<Category> findByName(String name);

    /**
     * Existence check before creating a new category, to avoid
     * relying on a DataIntegrityViolationException for the unique
     * constraint as the primary validation mechanism.
     */
    boolean existsByName(String name);
}
