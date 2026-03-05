package com.stardrop.dto;

import jakarta.validation.constraints.NotBlank;

public record OrderRequest(
    @NotBlank String shippingAddress
) {}
