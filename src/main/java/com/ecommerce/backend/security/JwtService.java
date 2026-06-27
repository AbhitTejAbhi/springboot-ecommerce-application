package com.ecommerce.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Issues and validates JWTs using the JJWT 0.12.x API.
 *
 * Secret and expiration are externalized to application properties
 * (never hardcoded) — see the required properties in
 * application.yml/properties:
 *
 *   jwt.secret=<Base64-encoded secret, decoding to at least 256 bits (32 bytes)>
 *   jwt.expiration-ms=86400000   # 24 hours, in milliseconds
 *
 * jwt.secret is expected to be a Base64-encoded string. getSigningKey()
 * Base64-decodes it before building the HMAC signing key — it does
 * NOT use the raw UTF-8 bytes of the property value. Generate a
 * compliant value with, e.g.:
 *   openssl rand -base64 32
 *
 * The decoded key MUST be at least 256 bits, since this service signs
 * with HS256. A short/weak secret here is a critical vulnerability —
 * it would let an attacker forge valid tokens.
 */
@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    /**
     * Generates a signed JWT for the given user, with email as the
     * subject and the user's role embedded as a custom claim so the
     * filter can authorize requests without re-querying the database
     * on every call.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(extraClaims);

        if (userDetails instanceof CustomUserDetails customUserDetails) {
            claims.put("userId", customUserDetails.getId());
            claims.put("role", customUserDetails.getUser().getRole().getName().name());
        }

        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts the username (subject) embedded in the token.
     * Named to match Spring Security's UserDetails#getUsername()
     * convention, even though in this domain the "username" is the
     * user's email address.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        Object userId = extractAllClaims(token).get("userId");
        return userId != null ? Long.valueOf(userId.toString()) : null;
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Validates the token's signature/structure and confirms it belongs
     * to the given user and has not expired. Any parsing failure
     * (malformed token, bad signature, unsupported token, expired
     * token) is treated as "invalid" rather than propagating the raw
     * JJWT exception up to the filter — the filter only needs a
     * boolean answer.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (ExpiredJwtException ex) {
            log.warn("JWT token expired: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.warn("Malformed JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.warn("Unsupported JWT token: {}", ex.getMessage());
        } catch (SignatureException ex) {
            log.warn("Invalid JWT signature: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.warn("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        // jwt.secret is stored as a Base64-encoded string (per the
        // class-level javadoc and generated via e.g. `openssl rand
        // -base64 32`), so it must be Base64-decoded back to raw bytes
        // before being used as the HMAC key — using the UTF-8 bytes of
        // the Base64 *text* directly would silently produce a
        // different, weaker key than the one actually intended.
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}