package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.CreateAddressRequest;
import com.ecommerce.backend.dto.request.UpdateAddressRequest;
import com.ecommerce.backend.dto.response.AddressResponse;
import com.ecommerce.backend.dto.response.ApiResponse;
import com.ecommerce.backend.security.CustomUserDetails;
import com.ecommerce.backend.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Customer-only address book endpoints, mapped under
 * /api/customer/addresses. Authorization is enforced by SecurityConfig
 * (.requestMatchers("/api/customer/**").hasRole("CUSTOMER")) — this
 * controller only implements the business behavior.
 *
 * The logged-in user's id is resolved from the JWT-authenticated
 * principal via @AuthenticationPrincipal, never accepted as a request
 * parameter — a customer can only ever act on their own addresses.
 */
@Tag(name = "Address", description = "Customer address book — create, view, update, delete")
@RestController
@RequestMapping("/api/customer/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "Create a new address",
            description = "If isDefault is true, any existing default address for this user is unset.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Address created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateAddressRequest request) {

        AddressResponse response = addressService.createAddress(userDetails.getId(), request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Address created successfully", response));
    }

    @Operation(summary = "Get all of the logged-in customer's addresses")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Addresses fetched successfully")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getMyAddresses(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<AddressResponse> response = addressService.getMyAddresses(userDetails.getId());

        return ResponseEntity
                .ok(ApiResponse.success("Addresses fetched successfully", response));
    }

    @Operation(summary = "Get a single address by id",
            description = "Only the owner of the address can access it.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Address fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found or not owned by caller")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {

        AddressResponse response = addressService.getAddressById(userDetails.getId(), id);

        return ResponseEntity
                .ok(ApiResponse.success("Address fetched successfully", response));
    }

    @Operation(summary = "Update an address",
            description = "If isDefault is set to true, any other default address for this user is unset.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Address updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found or not owned by caller")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody UpdateAddressRequest request) {

        AddressResponse response = addressService.updateAddress(userDetails.getId(), id, request);

        return ResponseEntity
                .ok(ApiResponse.success("Address updated successfully", response));
    }

    @Operation(summary = "Delete an address")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Address deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found or not owned by caller")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {

        addressService.deleteAddress(userDetails.getId(), id);

        return ResponseEntity
                .ok(ApiResponse.success("Address deleted successfully"));
    }
}