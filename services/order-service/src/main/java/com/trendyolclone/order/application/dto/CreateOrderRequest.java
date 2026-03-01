package com.trendyolclone.order.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateOrderRequest(
    @NotNull(message = "Shipping address ID is required")
    UUID shippingAddressId,

    String notes
) {}
