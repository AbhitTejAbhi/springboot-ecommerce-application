package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    /**
     * All addresses saved by a given user (e.g. address-book screen).
     * Backed by idx_address_user.
     */
    List<Address> findByUserId(Long userId);

    /**
     * Fetch a specific address only if it belongs to the given user.
     * This is the ownership-check pattern: used when a user requests
     * an address by id (e.g. to edit/delete/select at checkout) so a
     * user can never access another user's address by guessing an id —
     * the repository itself enforces the boundary instead of relying
     * on a separate authorization check after a plain findById.
     */
    Optional<Address> findByIdAndUserId(Long id, Long userId);

    /**
     * Lightweight ownership existence check — used where only a
     * boolean is needed (e.g. validating ownership before a bulk or
     * cascading operation) without hydrating the full entity.
     */
    boolean existsByIdAndUserId(Long id, Long userId);

    /**
     * Fetch the user's current default shipping address, if any.
     * Used by createAddress()/updateAddress() to find and unset the
     * previous default before a new address becomes the default —
     * enforces "only one default address per user" at the service layer.
     */
    Optional<Address> findByUserIdAndIsDefaultTrue(Long userId);

    /**
     * All addresses for a user, newest first — used for the
     * address-book listing screen so the most recently added address
     * appears at the top.
     */
    List<Address> findByUserIdOrderByCreatedAtDesc(Long userId);
    /**
     * Finds an address with the exact same physical location already
     * saved by the given user. Used during address creation to prevent
     * duplicate addresses being added to a customer's address book.
     *
     * The duplicate check is based only on the delivery location
     * (house number, street, city, state, country and pincode) and
     * intentionally ignores fields like phone number and the default
     * flag, since those may legitimately differ for the same address.
     */
    Optional<Address> findByUserIdAndHouseNumberAndStreetAndCityAndStateAndCountryAndPincode(
            Long userId,
            String houseNumber,
            String street,
            String city,
            String state,
            String country,
            String pincode
    );
}