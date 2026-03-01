package com.trendyolclone.product.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateProductRequest(
        @NotBlank(message = "Product name is required")
        String name,

        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
        BigDecimal price,

        BigDecimal discountedPrice,

        @NotNull(message = "Stock quantity is required")
        @Min(value = 0, message = "Stock quantity must be at least 0")
        Integer stockQuantity,

        @NotBlank(message = "SKU is required")
        String sku,

        @NotNull(message = "Category ID is required")
        UUID categoryId,

        String brand,

        List<@Valid ProductImageRequest> images
) {
}
