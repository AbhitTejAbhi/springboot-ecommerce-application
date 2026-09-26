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
 * Inbound payload for PUT /api/customer/addresses/{id}.
 * Same fields and exact same validation as CreateAddressRequest —
 * every field is editable, so a separate update DTO keeps this API
 * consistent with the rest of the project (mirrors how Product/
 * Category handle create vs update).
 */

@Schema(name = "Update Address Request", description = "Payload for updating an existing shipping address. All fields are required.")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAddressRequest {

    @Schema(description = "Updated house or flat number", example = "15A", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "House number is required")
    @Size(max = 20)
    private String houseNumber;

    @Schema(description = "Updated street name or locality", example = "Brigade Road", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Street is required")
    @Size(max = 200)
    private String street;

    @Schema(description = "Updated city", example = "Bengaluru", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "City is required")
    @Size(max = 100)
    private String city;

    @Schema(description = "Updated state", example = "Karnataka", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "State is required")
    @Size(max = 100)
    private String state;

    @Schema(description = "Updated country", example = "India", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Country is required")
    @Size(max = 100)
    private String country;

    @Schema(description = "Updated PIN/Postal code (4-10 digits)", example = "560001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[0-9]{4,10}$", message = "Pincode must be numeric and 4-10 digits long")
    private String pincode;

    @Schema(description = "Updated 10-digit mobile number", example = "9876543210", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    @Schema(description = "Set as default shipping address. Any previous default will be automatically unset.", example = "true")
    private boolean isDefault;
}