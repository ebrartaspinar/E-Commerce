package com.trendyolclone.product.application.service;

import com.trendyolclone.common.exception.ResourceNotFoundException;
import com.trendyolclone.product.application.dto.CategoryResponse;
import com.trendyolclone.product.domain.model.Category;
import com.trendyolclone.product.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getCategoryTree() {
        List<Category> rootCategories = categoryRepository.findByParentCategoryIsNullAndIsActiveTrue();
        return rootCategories.stream()
                .map(this::mapToCategoryResponse)
                .toList();
    }

    public CategoryResponse getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", slug));
        return mapToCategoryResponse(category);
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        List<CategoryResponse> childResponses = category.getChildren() != null
                ? category.getChildren().stream()
                    .filter(Category::isActive)
                    .map(this::mapToCategoryResponse)
                    .toList()
                : Collections.emptyList();

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getParentCategory() != null ? category.getParentCategory().getId() : null,
                category.getLevel(),
                category.isActive(),
                childResponses
        );
    }
}
