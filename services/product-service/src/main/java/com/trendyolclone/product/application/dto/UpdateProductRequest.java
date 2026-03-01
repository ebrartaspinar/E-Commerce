package com.trendyolclone.product.application.dto;

import com.trendyolclone.product.domain.model.ProductStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record UpdateProductRequest(
        String name,
        String description,
        BigDecimal price,
        BigDecimal discountedPrice,
        Integer stockQuantity,
        String sku,
        UUID categoryId,
        String brand,
        ProductStatus status,
        List<ProductImageRequest> images
) {
}
