package com.trendyolclone.product.application.dto;

import com.trendyolclone.product.domain.model.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        UUID sellerId,
        String name,
        String description,
        BigDecimal price,
        BigDecimal discountedPrice,
        Integer stockQuantity,
        String sku,
        ProductStatus status,
        CategoryResponse category,
        String brand,
        List<ProductImageResponse> images,
        Double averageRating,
        Integer reviewCount,
        LocalDateTime createdAt
) {
}
