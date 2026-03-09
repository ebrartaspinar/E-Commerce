package com.ecommerce.product.application.dto;

import com.ecommerce.product.domain.model.ProductStatus;

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
