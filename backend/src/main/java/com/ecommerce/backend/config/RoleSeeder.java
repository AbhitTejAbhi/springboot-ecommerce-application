package com.ecommerce.backend.config;

import com.ecommerce.backend.entity.Role;
import com.ecommerce.backend.enums.RoleName;
import com.ecommerce.backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Seeds the "roles" table on application startup, ensuring both
 * ADMIN and CUSTOMER exist before any registration/login request
 * can be served.
 *
 * Idempotent by design: each role is only inserted if it doesn't
 * already exist (checked via RoleRepository#findByName), so this
 * is safe to run on every application startup without creating
 * duplicate rows or failing on the unique constraint.
 *
 * @Order(1) guarantees this runs before AdminInitializer (@Order(2)),
 * since the ADMIN role must exist before AdminInitializer can assign
 * it to the default admin user.
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        seedRole(RoleName.ADMIN);
        seedRole(RoleName.CUSTOMER);
    }

    private void seedRole(RoleName roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = Role.builder()
                    .name(roleName)
                    .build();
            roleRepository.save(role);
            log.info("Seeded missing role: {}", roleName);
        } else {
            log.debug("Role already exists, skipping seed: {}", roleName);
        }
    }
}