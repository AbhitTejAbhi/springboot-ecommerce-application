package com.ecommerce.backend.security;

import com.ecommerce.backend.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Adapts the application's {@link User} entity to Spring Security's
 * {@link UserDetails} contract.
 *
 * Kept as a thin wrapper rather than making User itself implement
 * UserDetails — that would couple the persistence entity to the
 * security framework and pull Spring Security types into the entity
 * layer, which is poor separation of concerns.
 *
 * Exposes the underlying User (and its id) via {@link #getUser()} /
 * {@link #getId()} so downstream code (e.g. JwtService, controllers
 * pulling the authenticated principal) doesn't need to re-query the
 * database just to get the user's id.
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring Security's hasRole("X") checks for authority "ROLE_X",
        // so the enum name must be prefixed with "ROLE_" here.
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().name())
        );
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // Email is the natural login identifier in this domain model —
        // there is no separate "username" field on User.
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}