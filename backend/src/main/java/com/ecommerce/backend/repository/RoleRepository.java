package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.Role;
import com.ecommerce.backend.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    /**
     * Lookup a Role by its enum name — used when assigning a role
     * to a User during registration or admin user management.
     * "name" has a unique constraint + DB index (idx_role_name).
     */
    Optional<Role> findByName(RoleName name);
}
