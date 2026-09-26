package com.ecommerce.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Inbound payload for POST /api/customer/addresses.
 * Only user-editable address fields — no "id", no "userId", no
 * timestamps, and no order information. The owning user is resolved
 * server-side from the authenticated principal, never from the client.
 */

@Schema(name = "Create Address Request", description = "Payload for saving a new shipping address")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAddressRequest {

    @Schema(description = "House or flat number", example = "12B", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "House number is required")
    @Size(max = 20, message = "House number must not exceed 20 characters")
    private String houseNumber;

    @Schema(description = "Street name or locality", example = "MG Road", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Street is required")
    @Size(max = 200, message = "Street must not exceed 200 characters")
    private String street;

    @Schema(description = "City name", example = "Bengaluru", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "City is required")
    @Size(max = 100)
    private String city;

    @Schema(description = "State or province", example = "Karnataka", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "State is required")
    @Size(max = 100)
    private String state;

    @Schema(description = "Country name", example = "India", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Country is required")
    @Size(max = 100)
    private String country;

    @Schema(description = "PIN/Postal code (4-10 digits)", example = "560001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[0-9]{4,10}$", message = "Pincode must be numeric and 4-10 digits long")
    private String pincode;

    @Schema(description = "10-digit mobile number", example = "9876543210", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    @Schema(description = "Set as default shipping address. Any previous default will be automatically unset.", example = "false", defaultValue = "false")
    @Builder.Default
    private boolean isDefault = false;
}