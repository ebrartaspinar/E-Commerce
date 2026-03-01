package com.trendyolclone.product.domain.event;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductUpdatedEvent(
        UUID productId,
        String name,
        BigDecimal price,
        Integer stockQuantity
) {
}
