package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.CreateAddressRequest;
import com.ecommerce.backend.dto.request.UpdateAddressRequest;
import com.ecommerce.backend.dto.response.AddressResponse;
import com.ecommerce.backend.dto.response.ApiResponse;
import com.ecommerce.backend.security.CustomUserDetails;
import com.ecommerce.backend.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "4. Address", description = "Customer address book — create, view, update, delete shipping addresses")
@RestController
@RequestMapping("/api/customer/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "Create a new address",
            description = "Creates a new shipping address. If isDefault is true, any existing default " +
                    "address for this customer is automatically unset. Duplicate physical addresses " +
                    "for the same customer are not allowed."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Address created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed or duplicate address detected"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Customer role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateAddressRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Address created successfully",
                        addressService.createAddress(userDetails.getId(), request)
                ));
    }
    @Operation(summary = "Get all my addresses", description = "Returns all addresses for the logged-in customer, newest first.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Addresses fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Customer role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getMyAddresses(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Addresses fetched successfully",
                addressService.getMyAddresses(userDetails.getId())));
    }

    @Operation(summary = "Get a single address by ID", description = "Only the owner of the address can access it.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Address fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Customer role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found or not owned by caller"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressById(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID of the address", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Address fetched successfully",
                addressService.getAddressById(userDetails.getId(), id)));
    }

    @Operation(
            summary = "Update an address",
            description = "Updates an existing shipping address. If isDefault is set to true, any " +
                    "other default address for the customer is automatically unset."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Address updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Customer role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found or not owned by caller"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID of the address to update", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateAddressRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success("Address updated successfully",
                        addressService.updateAddress(userDetails.getId(), id, request)
                )
        );
    }

    @Operation(summary = "Delete an address")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Address deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Customer role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found or not owned by caller"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID of the address to delete", required = true, example = "1")
            @PathVariable Long id) {
        addressService.deleteAddress(userDetails.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully"));
    }
}
