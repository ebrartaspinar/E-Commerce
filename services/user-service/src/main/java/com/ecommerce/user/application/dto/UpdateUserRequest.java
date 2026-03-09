package com.ecommerce.user.application.dto;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        String phone
) {
}
