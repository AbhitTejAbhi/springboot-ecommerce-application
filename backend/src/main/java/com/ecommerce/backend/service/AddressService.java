package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.CreateAddressRequest;
import com.ecommerce.backend.dto.request.UpdateAddressRequest;
import com.ecommerce.backend.dto.response.AddressResponse;

import java.util.List;

/**
 * Address book contract for the logged-in customer. Every method is
 * scoped to a specific userId (resolved server-side from the
 * authenticated principal) — a customer only ever operates on their
 * own addresses.
 */
public interface AddressService {

    AddressResponse createAddress(Long userId, CreateAddressRequest request);

    List<AddressResponse> getMyAddresses(Long userId);

    AddressResponse getAddressById(Long userId, Long addressId);

    AddressResponse updateAddress(Long userId, Long addressId, UpdateAddressRequest request);

    void deleteAddress(Long userId, Long addressId);
}