package com.trendyolclone.user.api.controller;

import com.trendyolclone.common.dto.ApiResponse;
import com.trendyolclone.user.application.dto.AddressRequest;
import com.trendyolclone.user.application.dto.AddressResponse;
import com.trendyolclone.user.application.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/me/addresses")
@RequiredArgsConstructor
@Tag(name = "Addresses", description = "User address management endpoints")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    @Operation(summary = "List all addresses for the current user")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getUserAddresses() {
        UUID userId = getAuthenticatedUserId();
        List<AddressResponse> addresses = addressService.getUserAddresses(userId);
        return ResponseEntity.ok(ApiResponse.ok(addresses));
    }

    @PostMapping
    @Operation(summary = "Add a new address for the current user")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(@Valid @RequestBody AddressRequest request) {
        UUID userId = getAuthenticatedUserId();
        AddressResponse response = addressService.addAddress(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response, "Address added successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing address")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable UUID id,
            @Valid @RequestBody AddressRequest request
    ) {
        UUID userId = getAuthenticatedUserId();
        AddressResponse response = addressService.updateAddress(userId, id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Address updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an address")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable UUID id) {
        UUID userId = getAuthenticatedUserId();
        addressService.deleteAddress(userId, id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Address deleted successfully"));
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UUID) authentication.getPrincipal();
    }
}
