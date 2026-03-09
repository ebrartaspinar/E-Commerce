package com.ecommerce.product.application.dto;

import java.util.UUID;

public record ProductImageResponse(
        UUID id,
        String url,
        String altText,
        Integer sortOrder,
        boolean isMain
) {
}
