package com.ecommerce.backend.dto.response;

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
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    private Long addressId;
    private String houseNumber;
    private String street;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private String phoneNumber;
    private boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}