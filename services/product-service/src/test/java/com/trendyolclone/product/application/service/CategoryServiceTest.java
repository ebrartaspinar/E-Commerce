package com.trendyolclone.product.application.service;

import com.trendyolclone.common.exception.ResourceNotFoundException;
import com.trendyolclone.product.application.dto.CategoryResponse;
import com.trendyolclone.product.domain.model.Category;
import com.trendyolclone.product.domain.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Unit Tests")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category rootCategory;
    private Category childCategory;
    private UUID rootCategoryId;
    private UUID childCategoryId;

    @BeforeEach
    void setUp() {
        rootCategoryId = UUID.randomUUID();
        childCategoryId = UUID.randomUUID();

        rootCategory = Category.builder()
                .name("Electronics")
                .slug("electronics")
                .level(1)
                .isActive(true)
                .children(new ArrayList<>())
                .build();
        rootCategory.setId(rootCategoryId);
        rootCategory.setCreatedAt(LocalDateTime.now());
        rootCategory.setUpdatedAt(LocalDateTime.now());

        childCategory = Category.builder()
                .name("Phones")
                .slug("phones")
                .parentCategory(rootCategory)
                .level(2)
                .isActive(true)
                .children(new ArrayList<>())
                .build();
        childCategory.setId(childCategoryId);
        childCategory.setCreatedAt(LocalDateTime.now());
        childCategory.setUpdatedAt(LocalDateTime.now());

        rootCategory.getChildren().add(childCategory);
    }

    @Nested
    @DisplayName("getCategoryTree")
    class GetCategoryTree {

        @Test
        @DisplayName("Should return hierarchical category tree")
        void shouldReturnHierarchicalCategoryTree() {
            // given
            when(categoryRepository.findByParentCategoryIsNullAndIsActiveTrue())
                    .thenReturn(List.of(rootCategory));

            // when
            List<CategoryResponse> result = categoryService.getCategoryTree();

            // then
            assertThat(result).hasSize(1);
            CategoryResponse root = result.get(0);
            assertThat(root.id()).isEqualTo(rootCategoryId);
            assertThat(root.name()).isEqualTo("Electronics");
            assertThat(root.slug()).isEqualTo("electronics");
            assertThat(root.parentId()).isNull();
            assertThat(root.level()).isEqualTo(1);
            assertThat(root.isActive()).isTrue();
            assertThat(root.children()).hasSize(1);

            CategoryResponse child = root.children().get(0);
            assertThat(child.id()).isEqualTo(childCategoryId);
            assertThat(child.name()).isEqualTo("Phones");
            assertThat(child.slug()).isEqualTo("phones");
            assertThat(child.parentId()).isEqualTo(rootCategoryId);
            assertThat(child.level()).isEqualTo(2);

            verify(categoryRepository).findByParentCategoryIsNullAndIsActiveTrue();
        }

        @Test
        @DisplayName("Should return empty list when no root categories exist")
        void shouldReturnEmptyListWhenNoRootCategories() {
            // given
            when(categoryRepository.findByParentCategoryIsNullAndIsActiveTrue())
                    .thenReturn(Collections.emptyList());

            // when
            List<CategoryResponse> result = categoryService.getCategoryTree();

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should filter out inactive child categories")
        void shouldFilterOutInactiveChildCategories() {
            // given
            Category inactiveChild = Category.builder()
                    .name("Tablets")
                    .slug("tablets")
                    .parentCategory(rootCategory)
                    .level(2)
                    .isActive(false)
                    .children(new ArrayList<>())
                    .build();
            inactiveChild.setId(UUID.randomUUID());
            inactiveChild.setCreatedAt(LocalDateTime.now());
            inactiveChild.setUpdatedAt(LocalDateTime.now());

            rootCategory.getChildren().add(inactiveChild);

            when(categoryRepository.findByParentCategoryIsNullAndIsActiveTrue())
                    .thenReturn(List.of(rootCategory));

            // when
            List<CategoryResponse> result = categoryService.getCategoryTree();

            // then
            assertThat(result).hasSize(1);
            // Only active child (Phones) should be present, inactive (Tablets) filtered
            assertThat(result.get(0).children()).hasSize(1);
            assertThat(result.get(0).children().get(0).name()).isEqualTo("Phones");
        }

        @Test
        @DisplayName("Should return multiple root categories")
        void shouldReturnMultipleRootCategories() {
            // given
            Category anotherRoot = Category.builder()
                    .name("Clothing")
                    .slug("clothing")
                    .level(1)
                    .isActive(true)
                    .children(new ArrayList<>())
                    .build();
            anotherRoot.setId(UUID.randomUUID());
            anotherRoot.setCreatedAt(LocalDateTime.now());
            anotherRoot.setUpdatedAt(LocalDateTime.now());

            when(categoryRepository.findByParentCategoryIsNullAndIsActiveTrue())
                    .thenReturn(List.of(rootCategory, anotherRoot));

            // when
            List<CategoryResponse> result = categoryService.getCategoryTree();

            // then
            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("getCategoryBySlug")
    class GetCategoryBySlug {

        @Test
        @DisplayName("Should return category when found by slug")
        void shouldReturnCategoryWhenFoundBySlug() {
            // given
            when(categoryRepository.findBySlug("electronics")).thenReturn(Optional.of(rootCategory));

            // when
            CategoryResponse result = categoryService.getCategoryBySlug("electronics");

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(rootCategoryId);
            assertThat(result.name()).isEqualTo("Electronics");
            assertThat(result.slug()).isEqualTo("electronics");
            assertThat(result.isActive()).isTrue();
            verify(categoryRepository).findBySlug("electronics");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when category slug not found")
        void shouldThrowResourceNotFoundExceptionWhenSlugNotFound() {
            // given
            when(categoryRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> categoryService.getCategoryBySlug("nonexistent"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Category")
                    .hasMessageContaining("slug");
        }

        @Test
        @DisplayName("Should return category with children populated")
        void shouldReturnCategoryWithChildrenPopulated() {
            // given
            when(categoryRepository.findBySlug("electronics")).thenReturn(Optional.of(rootCategory));

            // when
            CategoryResponse result = categoryService.getCategoryBySlug("electronics");

            // then
            assertThat(result.children()).isNotEmpty();
            assertThat(result.children()).hasSize(1);
            assertThat(result.children().get(0).name()).isEqualTo("Phones");
        }
    }
}
