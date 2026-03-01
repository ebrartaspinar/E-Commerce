package com.trendyolclone.cart.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CartItemResponse(
    String productId,
    String productName,
    BigDecimal price,
    int quantity,
    String imageUrl,
    String sellerId,
    LocalDateTime addedAt,
    boolean priceChanged,
    BigDecimal currentPrice,
    boolean available
) {}
