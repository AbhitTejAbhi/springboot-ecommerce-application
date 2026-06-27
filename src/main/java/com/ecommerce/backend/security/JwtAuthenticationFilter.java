package com.ecommerce.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Runs once per request, extracts and validates the JWT from the
 * Authorization header, and — if valid — populates the
 * SecurityContext so downstream authorization checks (hasRole,
 * authenticated(), etc.) see an authenticated principal.
 *
 * Extends OncePerRequestFilter (not GenericFilterBean) to guarantee
 * exactly one execution per request even across internal dispatches
 * (e.g. forwards), which matters for a filter that does DB-backed
 * authentication work.
 *
 * Registered in SecurityConfig via:
 *   .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
 * so JWT-based auth is resolved before Spring Security's default
 * username/password filter ever runs.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        // No bearer token present — let the request continue unauthenticated.
        // Spring Security's authorization rules (permitAll / authenticated /
        // hasRole) decide downstream whether that's acceptable for this path.
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(BEARER_PREFIX.length());

        try {
            final String userEmail = jwtService.extractUsername(jwt);

            // Only attempt authentication if no authentication is already
            // set on the context for this request — avoids redundant work
            // and respects any authentication already established earlier
            // in the chain.
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null, // credentials not needed post-authentication
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception ex) {
            // Any failure while parsing/validating the token (expired,
            // malformed, signature mismatch, user no longer exists, etc.)
            // simply leaves the request unauthenticated rather than
            // throwing — Spring Security's entry point / access-denied
            // handling then returns the appropriate 401/403 downstream.
            log.warn("JWT authentication failed: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}