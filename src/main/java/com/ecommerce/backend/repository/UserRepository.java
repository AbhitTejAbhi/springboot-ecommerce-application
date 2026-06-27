package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    /**
     * Used for authentication and lookup by email.
     * "email" has a unique constraint + DB index (idx_user_email),
     * so this resolves to an indexed point lookup.
     */
    Optional<User> findByEmail(String email);

    /**
     * Lightweight existence check for registration flows —
     * avoids hydrating the full entity just to check uniqueness.
     */
    boolean existsByEmail(String email);

    /**
     * Filter users by role name (e.g. list all CUSTOMER or ADMIN accounts).
     * Traverses the User -> Role association via the "name" field on Role.
     */
    Page<User> findByRole_Name(RoleName roleName, Pageable pageable);
}
