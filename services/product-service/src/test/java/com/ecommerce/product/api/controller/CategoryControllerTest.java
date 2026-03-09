package com.ecommerce.product.api.controller;

import com.ecommerce.common.dto.PagedResponse;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.product.application.dto.CategoryResponse;
import com.ecommerce.product.application.dto.ProductResponse;
import com.ecommerce.product.application.service.CategoryService;
import com.ecommerce.product.application.service.ProductService;
import com.ecommerce.product.domain.model.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.ecommerce.product.infrastructure.config.SecurityConfig;
import com.ecommerce.product.infrastructure.config.JwtAuthenticationFilter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = CategoryController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CategoryController Integration Tests")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private ProductService productService;

    private CategoryResponse rootCategory;
    private CategoryResponse childCategory;

    @BeforeEach
    void setUp() {
        UUID rootId = UUID.randomUUID();
        UUID childId = UUID.randomUUID();

        childCategory = new CategoryResponse(
                childId, "Phones", "phones", rootId, 2, true, Collections.emptyList()
        );

        rootCategory = new CategoryResponse(
                rootId, "Electronics", "electronics", null, 1, true, List.of(childCategory)
        );
    }

    @Nested
    @DisplayName("GET /api/v1/categories")
    class GetCategoryTree {

        @Test
        @DisplayName("Should return 200 with category tree")
        void shouldReturn200WithCategoryTree() throws Exception {
            // given
            when(categoryService.getCategoryTree()).thenReturn(List.of(rootCategory));

            // when / then
            mockMvc.perform(get("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].name").value("Electronics"))
                    .andExpect(jsonPath("$.data[0].slug").value("electronics"))
                    .andExpect(jsonPath("$.data[0].level").value(1))
                    .andExpect(jsonPath("$.data[0].children").isArray())
                    .andExpect(jsonPath("$.data[0].children[0].name").value("Phones"));
        }

        @Test
        @DisplayName("Should return 200 with empty list when no categories")
        void shouldReturn200WithEmptyListWhenNoCategories() throws Exception {
            // given
            when(categoryService.getCategoryTree()).thenReturn(Collections.emptyList());

            // when / then
            mockMvc.perform(get("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isEmpty());
        }

        @Test
        @DisplayName("Should return 200 with multiple root categories")
        void shouldReturn200WithMultipleRootCategories() throws Exception {
            // given
            CategoryResponse anotherRoot = new CategoryResponse(
                    UUID.randomUUID(), "Clothing", "clothing", null, 1, true, Collections.emptyList()
            );
            when(categoryService.getCategoryTree()).thenReturn(List.of(rootCategory, anotherRoot));

            // when / then
            mockMvc.perform(get("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/categories/{slug}/products")
    class GetProductsByCategory {

        @Test
        @DisplayName("Should return 200 with products for category")
        void shouldReturn200WithProductsForCategory() throws Exception {
            // given
            ProductResponse product = new ProductResponse(
                    UUID.randomUUID(), UUID.randomUUID(), "Phone", "A phone",
                    new BigDecimal("599.99"), null, 50, "SKU-PHONE",
                    ProductStatus.ACTIVE, childCategory, "Samsung",
                    Collections.emptyList(), 4.0, 5, LocalDateTime.now()
            );

            PagedResponse<ProductResponse> pagedResponse = new PagedResponse<>(
                    List.of(product), 0, 20, 1, 1, true, true
            );

            when(productService.getProductsByCategory(eq("phones"), any()))
                    .thenReturn(pagedResponse);

            // when / then
            mockMvc.perform(get("/api/v1/categories/{slug}/products", "phones")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].name").value("Phone"))
                    .andExpect(jsonPath("$.data.totalElements").value(1));
        }

        @Test
        @DisplayName("Should return 404 when category slug not found")
        void shouldReturn404WhenCategorySlugNotFound() throws Exception {
            // given
            when(productService.getProductsByCategory(eq("nonexistent"), any()))
                    .thenThrow(new ResourceNotFoundException("Category", "slug", "nonexistent"));

            // when / then
            mockMvc.perform(get("/api/v1/categories/{slug}/products", "nonexistent")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }
}
