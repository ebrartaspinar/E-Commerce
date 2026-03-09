package com.ecommerce.product.domain.event;

import java.util.UUID;

public record ProductStockUpdatedEvent(
        UUID productId,
        UUID sellerId,
        Integer stockQuantity,
        String productName
) {
}
