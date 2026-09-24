package com.ecommerce.backend.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token-Bucket Rate Limiting filter powered by Bucket4j.
 * Runs at the Spring Security boundary BEFORE JwtAuthenticationFilter.
 * Enforces per-IP and per-endpoint policies:
 *   - /api/auth/login        : 5 requests / 1 minute
 *   - /api/auth/register     : 5 requests / 10 minutes
 *   - /api/products/search   : 100 requests / 1 minute
 *   - Other endpoints        : 200 requests / 1 minute
 */
@Slf4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private enum PolicyType {
        LOGIN,
        REGISTER,
        SEARCH,
        GENERAL
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip H2 console or Swagger API docs
        if (path.startsWith("/h2-console") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIP(request);
        PolicyType policyType = resolvePolicy(path);
        String bucketKey = clientIp + ":" + policyType.name();

        Bucket bucket = buckets.computeIfAbsent(bucketKey, k -> createNewBucket(policyType));

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            log.warn("Rate limit exceeded for IP={} on path={} (Policy={})", clientIp, path, policyType);

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Retry-After", getRetryAfterSeconds(policyType));

            String jsonResponse = String.format(
                    "{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Too many requests for %s. Please wait before trying again.\"}",
                    path
            );

            response.getWriter().write(jsonResponse);
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || "unknown".equalsIgnoreCase(xfHeader)) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }

    private PolicyType resolvePolicy(String path) {
        if ("/api/auth/login".equals(path)) {
            return PolicyType.LOGIN;
        } else if ("/api/auth/register".equals(path)) {
            return PolicyType.REGISTER;
        } else if (path.startsWith("/api/products/search")) {
            return PolicyType.SEARCH;
        } else {
            return PolicyType.GENERAL;
        }
    }

    private Bucket createNewBucket(PolicyType policyType) {
        Bandwidth limit;
        switch (policyType) {
            case LOGIN:
                // 5 requests per 1 minute
                limit = Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)));
                break;
            case REGISTER:
                // 5 requests per 10 minutes
                limit = Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(10)));
                break;
            case SEARCH:
                // 100 requests per 1 minute
                limit = Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1)));
                break;
            case GENERAL:
            default:
                // 200 requests per 1 minute
                limit = Bandwidth.classic(200, Refill.greedy(200, Duration.ofMinutes(1)));
                break;
        }
        return Bucket.builder().addLimit(limit).build();
    }

    private String getRetryAfterSeconds(PolicyType policyType) {
        return switch (policyType) {
            case LOGIN -> "60";
            case REGISTER -> "600";
            case SEARCH, GENERAL -> "60";
        };
    }
}
