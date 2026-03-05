package com.stardrop.dto;

public record AuthResponse(
    String token,
    String email,
    String firstName,
    String role
) {}
