package com.trendyolclone.product.application.dto;

import jakarta.validation.constraints.NotBlank;

public record ProductImageRequest(
        @NotBlank(message = "Image URL is required")
        String url,

        String altText,

        Integer sortOrder,

        boolean isMain
) {
}
