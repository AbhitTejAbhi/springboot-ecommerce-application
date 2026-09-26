package com.ecommerce.backend.security;

import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Loads a User from the database by email and adapts it into a
 * {@link CustomUserDetails} for Spring Security's authentication
 * machinery (used by both the username/password login flow via
 * AuthenticationManager, and JWT-based request authentication via
 * JwtAuthenticationFilter).
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * @Transactional(readOnly = true) keeps the Hibernate session open
     * for the duration of this call so the lazily-loaded "role"
     * association (and anything else CustomUserDetails/CustomUserDetails
     * .getAuthorities() touches) can be resolved without a separate
     * LazyInitializationException, even though User.role is mapped
     * EAGER in this codebase — kept here defensively in case that
     * ever changes.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email));

        return new CustomUserDetails(user);
    }
}