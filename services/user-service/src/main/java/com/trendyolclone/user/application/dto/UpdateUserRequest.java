package com.trendyolclone.user.application.dto;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        String phone
) {
}
