package com.trendyolclone.product.domain.event;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductCreatedEvent(
        UUID productId,
        UUID sellerId,
        String name,
        BigDecimal price,
        String categorySlug
) {
}
