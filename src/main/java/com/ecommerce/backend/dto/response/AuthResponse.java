package com.ecommerce.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(name = "Auth Response", description = "Returned on successful registration or login — contains the JWT and basic user identity")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    @Schema(description = "Signed JWT to include in the Authorization header of subsequent requests", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
    @Schema(description = "Token type — always Bearer", example = "Bearer")
    private String tokenType;
    @Schema(description = "Internal user ID", example = "1")
    private Long userId;
    @Schema(description = "Full name of the authenticated user", example = "John Doe")
    private String name;
    @Schema(description = "Email address of the authenticated user", example = "john@example.com")
    private String email;
    @Schema(description = "Role assigned to this user", example = "CUSTOMER", allowableValues = {"ADMIN", "CUSTOMER"})
    private String role;
}
