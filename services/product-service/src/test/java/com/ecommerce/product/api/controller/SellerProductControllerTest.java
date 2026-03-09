package com.ecommerce.product.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecommerce.common.dto.PagedResponse;
import com.ecommerce.common.exception.BusinessRuleException;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.ecommerce.product.infrastructure.config.SecurityConfig;
import com.ecommerce.product.infrastructure.config.JwtAuthenticationFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = SellerProductController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("SellerProductController Integration Tests")
class SellerProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private ProductService productService;

    private UUID sellerId;
    private UUID productId;
    private UUID categoryId;
    private ProductResponse sampleProduct;

    @BeforeEach
    void setUp() {
        sellerId = UUID.randomUUID();
        productId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        CategoryResponse categoryResponse = new CategoryResponse(
                categoryId, "Electronics", "electronics", null, 1, true, Collections.emptyList()
        );

        sampleProduct = new ProductResponse(
                productId, sellerId, "Test Product", "Test Description",
                new BigDecimal("99.99"), new BigDecimal("79.99"), 100,
                "SKU-001", ProductStatus.ACTIVE, categoryResponse, "TestBrand",
                Collections.emptyList(), 0.0, 0, LocalDateTime.now()
        );

        // Set up security context with SELLER role
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        sellerId.toString(), null,
                        List.of(new SimpleGrantedAuthority("ROLE_SELLER"))
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Nested
    @DisplayName("GET /api/v1/seller/products")
    class GetSellerProducts {

        @Test
        @DisplayName("Should return 200 with seller products")
        void shouldReturn200WithSellerProducts() throws Exception {
            // given
            PagedResponse<ProductResponse> pagedResponse = new PagedResponse<>(
                    List.of(sampleProduct), 0, 20, 1, 1, true, true
            );

            when(productService.getSellerProducts(eq(sellerId), any()))
                    .thenReturn(pagedResponse);

            // when / then
            mockMvc.perform(get("/api/v1/seller/products")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].name").value("Test Product"))
                    .andExpect(jsonPath("$.data.totalElements").value(1));
        }

        @Test
        @DisplayName("Should return 200 with empty list when seller has no products")
        void shouldReturn200WithEmptyListWhenNoProducts() throws Exception {
            // given
            PagedResponse<ProductResponse> emptyResponse = new PagedResponse<>(
                    Collections.emptyList(), 0, 20, 0, 0, true, true
            );

            when(productService.getSellerProducts(eq(sellerId), any()))
                    .thenReturn(emptyResponse);

            // when / then
            mockMvc.perform(get("/api/v1/seller/products")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/seller/products")
    class CreateProduct {

        @Test
        @DisplayName("Should return 201 when product is created successfully")
        void shouldReturn201WhenProductCreatedSuccessfully() throws Exception {
            // given
            CreateProductRequest request = new CreateProductRequest(
                    "New Product", "Description", new BigDecimal("149.99"),
                    null, 50, "SKU-NEW", categoryId, "NewBrand", null
            );

            when(productService.createProduct(eq(sellerId), any(CreateProductRequest.class)))
                    .thenReturn(sampleProduct);

            // when / then
            mockMvc.perform(post("/api/v1/seller/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.name").value("Test Product"))
                    .andExpect(jsonPath("$.message").value("Product created successfully"));
        }

        @Test
        @DisplayName("Should return 404 when category not found during product creation")
        void shouldReturn404WhenCategoryNotFoundDuringCreation() throws Exception {
            // given
            UUID invalidCategoryId = UUID.randomUUID();
            CreateProductRequest request = new CreateProductRequest(
                    "New Product", "Description", new BigDecimal("149.99"),
                    null, 50, "SKU-NEW", invalidCategoryId, "NewBrand", null
            );

            when(productService.createProduct(eq(sellerId), any(CreateProductRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Category", "id", invalidCategoryId));

            // when / then
            mockMvc.perform(post("/api/v1/seller/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/seller/products/{id}")
    class UpdateProduct {

        @Test
        @DisplayName("Should return 200 when product is updated successfully")
        void shouldReturn200WhenProductUpdatedSuccessfully() throws Exception {
            // given
            UpdateProductRequest request = new UpdateProductRequest(
                    "Updated Product", "Updated Desc", new BigDecimal("199.99"),
                    null, null, null, null, null, null, null
            );

            ProductResponse updatedProduct = new ProductResponse(
                    productId, sellerId, "Updated Product", "Updated Desc",
                    new BigDecimal("199.99"), null, 100, "SKU-001",
                    ProductStatus.ACTIVE, sampleProduct.category(), "TestBrand",
                    Collections.emptyList(), 0.0, 0, LocalDateTime.now()
            );

            when(productService.updateProduct(eq(sellerId), eq(productId), any(UpdateProductRequest.class)))
                    .thenReturn(updatedProduct);

            // when / then
            mockMvc.perform(put("/api/v1/seller/products/{id}", productId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.name").value("Updated Product"))
                    .andExpect(jsonPath("$.message").value("Product updated successfully"));
        }

        @Test
        @DisplayName("Should return 422 when seller is not the owner")
        void shouldReturn422WhenSellerIsNotOwner() throws Exception {
            // given
            UpdateProductRequest request = new UpdateProductRequest(
                    "Updated Product", null, null, null, null, null, null, null, null, null
            );

            when(productService.updateProduct(eq(sellerId), eq(productId), any(UpdateProductRequest.class)))
                    .thenThrow(new BusinessRuleException("You are not authorized to update this product"));

            // when / then
            mockMvc.perform(put("/api/v1/seller/products/{id}", productId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/seller/products/{id}/stock")
    class UpdateStock {

        @Test
        @DisplayName("Should return 200 when stock is updated successfully")
        void shouldReturn200WhenStockUpdatedSuccessfully() throws Exception {
            // given
            UpdateStockRequest request = new UpdateStockRequest(50);

            ProductResponse updatedProduct = new ProductResponse(
                    productId, sellerId, "Test Product", "Test Description",
                    new BigDecimal("99.99"), null, 50, "SKU-001",
                    ProductStatus.ACTIVE, sampleProduct.category(), "TestBrand",
                    Collections.emptyList(), 0.0, 0, LocalDateTime.now()
            );

            when(productService.updateStock(eq(sellerId), eq(productId), any(UpdateStockRequest.class)))
                    .thenReturn(updatedProduct);

            // when / then
            mockMvc.perform(patch("/api/v1/seller/products/{id}/stock", productId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.stockQuantity").value(50))
                    .andExpect(jsonPath("$.message").value("Stock updated successfully"));
        }

        @Test
        @DisplayName("Should return 404 when product not found for stock update")
        void shouldReturn404WhenProductNotFoundForStockUpdate() throws Exception {
            // given
            UUID nonExistentId = UUID.randomUUID();
            UpdateStockRequest request = new UpdateStockRequest(50);

            when(productService.updateStock(eq(sellerId), eq(nonExistentId), any(UpdateStockRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Product", "id", nonExistentId));

            // when / then
            mockMvc.perform(patch("/api/v1/seller/products/{id}/stock", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/seller/products/{id}")
    class DeleteProduct {

        @Test
        @DisplayName("Should return 200 when product is deleted successfully")
        void shouldReturn200WhenProductDeletedSuccessfully() throws Exception {
            // given
            doNothing().when(productService).deleteProduct(sellerId, productId);

            // when / then
            mockMvc.perform(delete("/api/v1/seller/products/{id}", productId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Product deleted successfully"));
        }

        @Test
        @DisplayName("Should return 404 when product not found for deletion")
        void shouldReturn404WhenProductNotFoundForDeletion() throws Exception {
            // given
            UUID nonExistentId = UUID.randomUUID();
            doThrow(new ResourceNotFoundException("Product", "id", nonExistentId))
                    .when(productService).deleteProduct(sellerId, nonExistentId);

            // when / then
            mockMvc.perform(delete("/api/v1/seller/products/{id}", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 422 when seller is not the owner for deletion")
        void shouldReturn422WhenSellerIsNotOwnerForDeletion() throws Exception {
            // given
            doThrow(new BusinessRuleException("You are not authorized to delete this product"))
                    .when(productService).deleteProduct(sellerId, productId);

            // when / then
            mockMvc.perform(delete("/api/v1/seller/products/{id}", productId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity());
        }
    }
}
