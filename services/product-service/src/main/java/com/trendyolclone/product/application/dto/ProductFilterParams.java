package com.trendyolclone.product.application.dto;

import com.trendyolclone.product.domain.model.ProductStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductFilterParams(
        UUID categoryId,
        String brand,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        ProductStatus status,
        String search
) {
}
