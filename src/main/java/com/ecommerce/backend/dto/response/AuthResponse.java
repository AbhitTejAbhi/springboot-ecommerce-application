package com.ecommerce.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Outbound payload returned after successful login/registration.
 *
 * "password" is never included — only the issued token plus the
 * minimal user identity/claims a client needs to populate its session
 * (e.g. showing the logged-in user's name, and the role for client-side
 * UI gating; the server still re-validates authorization on every request).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String tokenType;
    private Long userId;
    private String name;
    private String email;
    private String role;
}
