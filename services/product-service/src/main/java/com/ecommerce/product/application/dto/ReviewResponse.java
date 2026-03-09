package com.ecommerce.product.application.dto;

import com.ecommerce.product.domain.model.ReviewStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewResponse(
        UUID id,
        UUID productId,
        UUID userId,
        Integer rating,
        String comment,
        ReviewStatus status,
        LocalDateTime createdAt
) {
}
