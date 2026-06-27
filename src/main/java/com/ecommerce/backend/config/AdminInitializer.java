package com.ecommerce.backend.config;

import com.ecommerce.backend.entity.Role;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.enums.RoleName;
import com.ecommerce.backend.repository.RoleRepository;
import com.ecommerce.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Bootstraps a single default admin user on application startup.
 * Single responsibility only: check if it exists, create it if not.
 *
 * No service layer, no controller, no authentication, no JWT — this
 * is bootstrap code, not business logic.
 *
 * @Order(2) ensures this runs after RoleSeeder (@Order(1) or
 * otherwise earlier), since the ADMIN role must already exist in
 * the database before this can assign it to the default admin user.
 * If RoleSeeder has no explicit @Order, give it @Order(1) so the
 * relative ordering is guaranteed rather than left to chance.
 */
@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private static final String DEFAULT_ADMIN_EMAIL = "admin@gmail.com";
    private static final String DEFAULT_ADMIN_NAME = "Admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "Admin@123";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail(DEFAULT_ADMIN_EMAIL)) {
            log.info("Default admin already exists.");
            return;
        }

        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseThrow(() -> new IllegalStateException(
                        "ADMIN role not found — ensure RoleSeeder runs before AdminInitializer"));

        User admin = User.builder()
                .name(DEFAULT_ADMIN_NAME)
                .email(DEFAULT_ADMIN_EMAIL)
                .password(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD))
                .role(adminRole)
                .enabled(true)
                .build();

        userRepository.save(admin);

        log.info("Default admin created successfully.");
    }
}