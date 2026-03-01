package com.trendyolclone.product.application.dto;

import java.util.List;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String slug,
        UUID parentId,
        Integer level,
        boolean isActive,
        List<CategoryResponse> children
) {
}
