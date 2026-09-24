package com.ecommerce.backend.config;

import com.ecommerce.backend.security.CustomUserDetailsService;
import com.ecommerce.backend.security.JwtAuthenticationFilter;
import com.ecommerce.backend.security.RateLimitingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Final, production-stage Spring Security configuration.
 *
 * This class is the merged end-state of all four build stages:
 *
 *   Stage 1 — base config: CSRF disabled, H2 console allowed, permissive rules.
 *   Stage 2 — CustomUserDetailsService wired in for DB-backed authentication.
 *   Stage 3 — JwtAuthenticationFilter + JwtService wired in to validate
 *             bearer tokens on every request.
 *   Stage 4 — endpoint-level role-based authorization rules.
 *
 * Session policy is STATELESS: JWT carries all auth state on every
 * request, so the server never creates or relies on an HttpSession.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RateLimitingFilter rateLimitingFilter;

    /**
     * BCrypt is the standard, adaptive, salted hashing algorithm for
     * password storage — never store or compare plaintext passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Wires CustomUserDetailsService + the PasswordEncoder together so
     * Spring Security knows how to look up a user and verify their
     * password during the login (AuthenticationManager.authenticate)
     * flow used by the /api/auth/login endpoint.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Exposed as a bean so the auth service/controller can inject it
     * and call authenticate(...) directly when handling /api/auth/login,
     * rather than manually re-implementing credential checking.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Minimal CORS configuration so the API can be called from a
     * separate frontend origin. Tighten allowedOrigins to the actual
     * deployed frontend URL(s) in production — "*" is shown here only
     * as a placeholder and should not ship as-is to production.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF protection is meaningless for a stateless, token-based
                // API with no browser session/cookie-based auth — disabling
                // it is correct here, not a shortcut.
                .csrf(csrf -> csrf.disable())

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // H2 console renders itself inside an HTML frame; Spring
                // Security's default frame-options policy (DENY) would
                // otherwise block it from loading in the browser.
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                )

                .authorizeHttpRequests(auth -> auth
                        // Public auth endpoints (register/login) — no token required.
                        .requestMatchers("/api/auth/**").permitAll()

                        // H2 console — dev/test convenience only. Ensure this is
                        // disabled/removed entirely before a real production
                        // deployment; it should never be exposed publicly.
                        .requestMatchers("/h2-console/**").permitAll()

                        // Swagger/OpenAPI docs, if present — adjust/remove if
                        // API documentation isn't exposed in this project.
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Role-restricted areas.
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/customer/**").hasRole("CUSTOMER")

                        // Public category browsing — no login required.
                        // Scoped to GET only so this rule can never
                        // accidentally cover a future write endpoint
                        // under /api/categories/**.
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()

                        // Product browsing requires a logged-in user of any role,
                        // but not a specific role.
                        .requestMatchers("/api/products/**").permitAll()

                        // Default-deny: anything not explicitly listed above
                        // requires authentication. Safer default than permitAll.
                        .anyRequest().authenticated()
                )

                // No server-side session is ever created or used — every
                // request must carry its own valid JWT. This is what makes
                // the API horizontally scalable without sticky sessions.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authenticationProvider(authenticationProvider())

                // RateLimitingFilter runs at the API boundary before authentication
                .addFilterBefore(
                        rateLimitingFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                // JwtAuthenticationFilter runs before UsernamePasswordAuthenticationFilter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}