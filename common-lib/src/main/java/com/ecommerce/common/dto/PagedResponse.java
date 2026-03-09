package com.ecommerce.common.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public record PagedResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean last,
    boolean first
) {
    public static <T> PagedResponse<T> from(Page<T> springPage) {
        return new PagedResponse<>(
            springPage.getContent(),
            springPage.getNumber(),
            springPage.getSize(),
            springPage.getTotalElements(),
            springPage.getTotalPages(),
            springPage.isLast(),
            springPage.isFirst()
        );
    }
}
