package com.ecommerce.user.application.dto;

import com.ecommerce.user.domain.model.UserRole;
import com.ecommerce.user.domain.model.UserStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phone,
        UserRole role,
        UserStatus status,
        List<AddressResponse> addresses,
        LocalDateTime createdAt
) {
}
