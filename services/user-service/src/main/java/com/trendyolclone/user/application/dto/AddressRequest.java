package com.trendyolclone.user.application.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Full address is required")
        String fullAddress,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "District is required")
        String district,

        String postalCode,

        boolean isDefault
) {
}
