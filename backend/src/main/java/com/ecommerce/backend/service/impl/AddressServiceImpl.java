package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.request.CreateAddressRequest;
import com.ecommerce.backend.dto.request.UpdateAddressRequest;
import com.ecommerce.backend.dto.response.AddressResponse;
import com.ecommerce.backend.entity.Address;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.exception.BadRequestException;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.AddressRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * All address book business logic lives here — controllers stay thin
 * and only delegate to this layer. Scoped to AddressRepository and
 * UserRepository only, per current module boundaries (no
 * OrderRepository dependency / active-orders delete protection yet).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AddressResponse createAddress(Long userId, CreateAddressRequest request) {

        User user = getUserOrThrow(userId);
        // Validate that the customer is not saving an address that
        // already exists in their address book.
        validateDuplicateAddress(userId, request);

        // Only one address can be the default at a time. If the new
        // address is being created as the default, unset whatever
        // address currently holds that flag for this user BEFORE
        // saving the new one, so there is never a moment with two
        // default addresses persisted at once.
        if (request.isDefault()) {
            unsetExistingDefault(userId);
        }

        Address address = Address.builder()
                .houseNumber(request.getHouseNumber())
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .pincode(request.getPincode())
                .phoneNumber(request.getPhoneNumber())
                .isDefault(request.isDefault())
                .user(user)
                .build();

        Address savedAddress = addressRepository.save(address);

        log.info("Created address id={} for userId={}", savedAddress.getId(), userId);

        return mapToResponse(savedAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getMyAddresses(Long userId) {
        return addressRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Long userId, Long addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found with id: " + addressId));

        return mapToResponse(address);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId, UpdateAddressRequest request) {

        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found with id: " + addressId));

        // Same single-default-address rule as create: if this update
        // makes the address the default, unset any other address
        // currently holding that flag first. Skip unsetting if this
        // address itself was already the default (no-op, and avoids
        // unsetting itself via unsetExistingDefault's exclusion below).
        if (request.isDefault() && !address.isDefault()) {
            unsetExistingDefault(userId);
        }

        address.setHouseNumber(request.getHouseNumber());
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setCountry(request.getCountry());
        address.setPincode(request.getPincode());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setDefault(request.isDefault());

        Address updatedAddress = addressRepository.save(address);

        log.info("Updated address id={} for userId={}", addressId, userId);

        return mapToResponse(updatedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {

        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found with id: " + addressId));

        addressRepository.delete(address);

        log.info("Deleted address id={} for userId={}", addressId, userId);
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));
    }

    /**
     * Finds the user's current default address (if any) and clears
     * its flag. Used by both createAddress() and updateAddress() so
     * the "only one default address per user" rule can never drift
     * between the two entry points.
     */
    private void unsetExistingDefault(Long userId) {
        addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .ifPresent(existingDefault -> {
                    existingDefault.setDefault(false);
                    addressRepository.save(existingDefault);
                });
    }

    private AddressResponse mapToResponse(Address address) {
        return AddressResponse.builder()
                .addressId(address.getId())
                .houseNumber(address.getHouseNumber())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .pincode(address.getPincode())
                .phoneNumber(address.getPhoneNumber())
                .isDefault(address.isDefault())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
    /**
     * Prevents a customer from saving duplicate physical addresses.
     * Two addresses are considered duplicates when the same user
     * already has an address with identical house number, street,
     * city, state, country and pincode.
     *
     * Phone number and default flag are intentionally excluded from
     * the duplicate check because they may legitimately differ for
     * the same delivery location.
     */
    private void validateDuplicateAddress(Long userId,
                                          CreateAddressRequest request) {

        addressRepository
                .findByUserIdAndHouseNumberAndStreetAndCityAndStateAndCountryAndPincode(
                        userId,
                        request.getHouseNumber(),
                        request.getStreet(),
                        request.getCity(),
                        request.getState(),
                        request.getCountry(),
                        request.getPincode()
                )
                .ifPresent(existingAddress -> {
                    throw new BadRequestException(
                            "Address already exists for this user.");
                });
    }
}