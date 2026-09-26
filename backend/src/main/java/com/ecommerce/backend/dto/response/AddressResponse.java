package com.ecommerce.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Outbound representation of an Address. The entity is never returned
 * directly — the owning User entity, the user's password/role, and
 * the "orders" association are all deliberately excluded. No userId
 * is included either: customers only ever view their own addresses,
 * so it adds no value and is omitted per spec.
 */

@Schema(name = "Address Response", description = "Shipping address data. Owning user, orders, and role are never exposed.")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    @Schema(description = "Unique address ID", example = "1")
    private Long addressId;

    @Schema(description = "House or flat number", example = "12B")
    private String houseNumber;

    @Schema(description = "Street name or locality", example = "MG Road")
    private String street;

    @Schema(description = "City", example = "Bengaluru")
    private String city;

    @Schema(description = "State or province", example = "Karnataka")
    private String state;

    @Schema(description = "Country", example = "India")
    private String country;

    @Schema(description = "PIN/Postal code", example = "560001")
    private String pincode;

    @Schema(description = "10-digit mobile number for delivery contact", example = "9876543210")
    private String phoneNumber;

    @Schema(description = "Whether this is the user's default shipping address", example = "true")
    private boolean isDefault;

    @Schema(description = "Timestamp when this address was saved", example = "2024-06-01T14:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp of the last update", example = "2024-06-02T10:30:00")
    private LocalDateTime updatedAt;
}