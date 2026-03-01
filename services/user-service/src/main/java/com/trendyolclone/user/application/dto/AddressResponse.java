package com.trendyolclone.user.application.dto;

import java.util.UUID;

public record AddressResponse(
        UUID id,
        String title,
        String fullAddress,
        String city,
        String district,
        String postalCode,
        boolean isDefault
) {
}
