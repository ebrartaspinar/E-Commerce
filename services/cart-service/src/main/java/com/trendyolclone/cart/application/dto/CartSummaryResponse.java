package com.trendyolclone.cart.application.dto;

import java.math.BigDecimal;

public record CartSummaryResponse(
    BigDecimal totalAmount,
    int itemCount,
    String estimatedDelivery
) {}
