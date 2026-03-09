package com.ecommerce.product.api.controller;

import com.ecommerce.common.dto.PagedResponse;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.product.application.dto.*;
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
        value = ProductController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ProductController Integration Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private ProductService productService;

    private ProductResponse sampleProduct;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        UUID sellerId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        CategoryResponse categoryResponse = new CategoryResponse(
                categoryId, "Electronics", "electronics", null, 1, true, Collections.emptyList()
        );

        sampleProduct = new ProductResponse(
                productId, sellerId, "Test Product", "Test Description",
                new BigDecimal("99.99"), new BigDecimal("79.99"), 100,
                "SKU-001", ProductStatus.ACTIVE, categoryResponse, "TestBrand",
                List.of(new ProductImageResponse(UUID.randomUUID(), "http://img.jpg", "Alt", 0, true)),
                4.5, 10, LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("GET /api/v1/products")
    class GetProducts {

        @Test
        @DisplayName("Should return 200 with paged products")
        void shouldReturn200WithPagedProducts() throws Exception {
            // given
            PagedResponse<ProductResponse> pagedResponse = new PagedResponse<>(
                    List.of(sampleProduct), 0, 20, 1, 1, true, true
            );

            when(productService.getProducts(any(ProductFilterParams.class), any()))
                    .thenReturn(pagedResponse);

            // when / then
            mockMvc.perform(get("/api/v1/products")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].name").value("Test Product"))
                    .andExpect(jsonPath("$.data.content[0].price").value(99.99))
                    .andExpect(jsonPath("$.data.totalElements").value(1));
        }

        @Test
        @DisplayName("Should return 200 with empty page when no products")
        void shouldReturn200WithEmptyPageWhenNoProducts() throws Exception {
            // given
            PagedResponse<ProductResponse> emptyResponse = new PagedResponse<>(
                    Collections.emptyList(), 0, 20, 0, 0, true, true
            );

            when(productService.getProducts(any(ProductFilterParams.class), any()))
                    .thenReturn(emptyResponse);

            // when / then
            mockMvc.perform(get("/api/v1/products")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }

        @Test
        @DisplayName("Should accept filter parameters")
        void shouldAcceptFilterParameters() throws Exception {
            // given
            PagedResponse<ProductResponse> pagedResponse = new PagedResponse<>(
                    List.of(sampleProduct), 0, 20, 1, 1, true, true
            );

            when(productService.getProducts(any(ProductFilterParams.class), any()))
                    .thenReturn(pagedResponse);

            // when / then
            mockMvc.perform(get("/api/v1/products")
                            .param("brand", "TestBrand")
                            .param("minPrice", "10.00")
                            .param("maxPrice", "200.00")
                            .param("status", "ACTIVE")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/products/{id}")
    class GetProductById {

        @Test
        @DisplayName("Should return 200 with product when found")
        void shouldReturn200WithProductWhenFound() throws Exception {
            // given
            when(productService.getProductById(productId)).thenReturn(sampleProduct);

            // when / then
            mockMvc.perform(get("/api/v1/products/{id}", productId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(productId.toString()))
                    .andExpect(jsonPath("$.data.name").value("Test Product"))
                    .andExpect(jsonPath("$.data.price").value(99.99))
                    .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                    .andExpect(jsonPath("$.data.brand").value("TestBrand"));
        }

        @Test
        @DisplayName("Should return 404 when product not found")
        void shouldReturn404WhenProductNotFound() throws Exception {
            // given
            UUID nonExistentId = UUID.randomUUID();
            when(productService.getProductById(nonExistentId))
                    .thenThrow(new ResourceNotFoundException("Product", "id", nonExistentId));

            // when / then
            mockMvc.perform(get("/api/v1/products/{id}", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/products/search")
    class SearchProducts {

        @Test
        @DisplayName("Should return 200 with search results")
        void shouldReturn200WithSearchResults() throws Exception {
            // given
            PagedResponse<ProductResponse> searchResponse = new PagedResponse<>(
                    List.of(sampleProduct), 0, 20, 1, 1, true, true
            );

            when(productService.getProducts(any(ProductFilterParams.class), any()))
                    .thenReturn(searchResponse);

            // when / then
            mockMvc.perform(get("/api/v1/products/search")
                            .param("q", "Test")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].name").value("Test Product"));
        }

        @Test
        @DisplayName("Should return 200 with empty results when no match")
        void shouldReturn200WithEmptyResultsWhenNoMatch() throws Exception {
            // given
            PagedResponse<ProductResponse> emptyResponse = new PagedResponse<>(
                    Collections.emptyList(), 0, 20, 0, 0, true, true
            );

            when(productService.getProducts(any(ProductFilterParams.class), any()))
                    .thenReturn(emptyResponse);

            // when / then
            mockMvc.perform(get("/api/v1/products/search")
                            .param("q", "nonexistent")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty());
        }

        @Test
        @DisplayName("Should accept additional filter parameters in search")
        void shouldAcceptAdditionalFilterParametersInSearch() throws Exception {
            // given
            PagedResponse<ProductResponse> searchResponse = new PagedResponse<>(
                    List.of(sampleProduct), 0, 20, 1, 1, true, true
            );

            when(productService.getProducts(any(ProductFilterParams.class), any()))
                    .thenReturn(searchResponse);

            // when / then
            mockMvc.perform(get("/api/v1/products/search")
                            .param("q", "phone")
                            .param("brand", "Samsung")
                            .param("minPrice", "100")
                            .param("maxPrice", "1000")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }
}
