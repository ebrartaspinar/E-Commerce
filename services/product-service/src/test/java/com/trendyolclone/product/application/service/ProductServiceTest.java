package com.trendyolclone.product.application.service;

import com.trendyolclone.common.dto.PagedResponse;
import com.trendyolclone.common.exception.BusinessRuleException;
import com.trendyolclone.common.exception.ResourceNotFoundException;
import com.trendyolclone.product.application.dto.*;
import com.trendyolclone.product.domain.event.ProductCreatedEvent;
import com.trendyolclone.product.domain.event.ProductStockUpdatedEvent;
import com.trendyolclone.product.domain.event.ProductUpdatedEvent;
import com.trendyolclone.product.domain.model.*;
import com.trendyolclone.product.domain.repository.CategoryRepository;
import com.trendyolclone.product.domain.repository.ProductRepository;
import com.trendyolclone.product.infrastructure.kafka.ProductEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductEventProducer productEventProducer;

    @InjectMocks
    private ProductService productService;

    private UUID sellerId;
    private UUID productId;
    private UUID categoryId;
    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        sellerId = UUID.randomUUID();
        productId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        category = Category.builder()
                .name("Electronics")
                .slug("electronics")
                .level(1)
                .isActive(true)
                .children(new ArrayList<>())
                .build();
        category.setId(categoryId);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        product = Product.builder()
                .sellerId(sellerId)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .discountedPrice(new BigDecimal("79.99"))
                .stockQuantity(100)
                .sku("SKU-001")
                .status(ProductStatus.ACTIVE)
                .category(category)
                .brand("TestBrand")
                .averageRating(0.0)
                .reviewCount(0)
                .images(new ArrayList<>())
                .build();
        product.setId(productId);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("getProducts")
    class GetProducts {

        @Test
        @DisplayName("Should return paged products with no filters")
        void shouldReturnPagedProductsWithNoFilters() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            ProductFilterParams filterParams = new ProductFilterParams(null, null, null, null, null, null);
            Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

            when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);

            // when
            PagedResponse<ProductResponse> result = productService.getProducts(filterParams, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).name()).isEqualTo("Test Product");
            assertThat(result.totalElements()).isEqualTo(1);
            verify(productRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("Should return paged products with category filter")
        void shouldReturnPagedProductsWithCategoryFilter() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            ProductFilterParams filterParams = new ProductFilterParams(categoryId, null, null, null, null, null);
            Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

            when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);

            // when
            PagedResponse<ProductResponse> result = productService.getProducts(filterParams, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
        }

        @Test
        @DisplayName("Should return paged products with all filters applied")
        void shouldReturnPagedProductsWithAllFilters() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            ProductFilterParams filterParams = new ProductFilterParams(
                    categoryId, "TestBrand", new BigDecimal("10.00"), new BigDecimal("200.00"),
                    ProductStatus.ACTIVE, "Test"
            );
            Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

            when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);

            // when
            PagedResponse<ProductResponse> result = productService.getProducts(filterParams, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
        }

        @Test
        @DisplayName("Should return empty page when no products match")
        void shouldReturnEmptyPageWhenNoProductsMatch() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            ProductFilterParams filterParams = new ProductFilterParams(null, null, null, null, null, null);
            Page<Product> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

            // when
            PagedResponse<ProductResponse> result = productService.getProducts(filterParams, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("getProductById")
    class GetProductById {

        @Test
        @DisplayName("Should return product when found")
        void shouldReturnProductWhenFound() {
            // given
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));

            // when
            ProductResponse result = productService.getProductById(productId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(productId);
            assertThat(result.name()).isEqualTo("Test Product");
            assertThat(result.price()).isEqualByComparingTo(new BigDecimal("99.99"));
            assertThat(result.sellerId()).isEqualTo(sellerId);
            assertThat(result.status()).isEqualTo(ProductStatus.ACTIVE);
            assertThat(result.brand()).isEqualTo("TestBrand");
            verify(productRepository).findById(productId);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when product not found")
        void shouldThrowResourceNotFoundExceptionWhenProductNotFound() {
            // given
            UUID nonExistentId = UUID.randomUUID();
            when(productRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> productService.getProductById(nonExistentId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Product")
                    .hasMessageContaining("id");
        }
    }

    @Nested
    @DisplayName("createProduct")
    class CreateProduct {

        @Test
        @DisplayName("Should create product successfully with images")
        void shouldCreateProductSuccessfullyWithImages() {
            // given
            List<ProductImageRequest> imageRequests = List.of(
                    new ProductImageRequest("http://image1.jpg", "Image 1", 0, true),
                    new ProductImageRequest("http://image2.jpg", "Image 2", 1, false)
            );

            CreateProductRequest request = new CreateProductRequest(
                    "New Product", "Description", new BigDecimal("149.99"),
                    new BigDecimal("129.99"), 50, "SKU-NEW", categoryId, "NewBrand", imageRequests
            );

            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
                Product saved = invocation.getArgument(0);
                saved.setId(UUID.randomUUID());
                saved.setCreatedAt(LocalDateTime.now());
                saved.setUpdatedAt(LocalDateTime.now());
                return saved;
            });

            // when
            ProductResponse result = productService.createProduct(sellerId, request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("New Product");
            assertThat(result.price()).isEqualByComparingTo(new BigDecimal("149.99"));
            assertThat(result.status()).isEqualTo(ProductStatus.ACTIVE);
            assertThat(result.images()).hasSize(2);

            verify(categoryRepository).findById(categoryId);
            verify(productRepository).save(any(Product.class));
            verify(productEventProducer).publishProductCreated(any(ProductCreatedEvent.class));
        }

        @Test
        @DisplayName("Should create product successfully without images")
        void shouldCreateProductSuccessfullyWithoutImages() {
            // given
            CreateProductRequest request = new CreateProductRequest(
                    "New Product", "Description", new BigDecimal("149.99"),
                    null, 50, "SKU-NEW", categoryId, "NewBrand", null
            );

            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
                Product saved = invocation.getArgument(0);
                saved.setId(UUID.randomUUID());
                saved.setCreatedAt(LocalDateTime.now());
                saved.setUpdatedAt(LocalDateTime.now());
                return saved;
            });

            // when
            ProductResponse result = productService.createProduct(sellerId, request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.images()).isEmpty();
            verify(productEventProducer).publishProductCreated(any(ProductCreatedEvent.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when category not found")
        void shouldThrowResourceNotFoundExceptionWhenCategoryNotFound() {
            // given
            UUID invalidCategoryId = UUID.randomUUID();
            CreateProductRequest request = new CreateProductRequest(
                    "New Product", "Description", new BigDecimal("149.99"),
                    null, 50, "SKU-NEW", invalidCategoryId, "NewBrand", null
            );

            when(categoryRepository.findById(invalidCategoryId)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> productService.createProduct(sellerId, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Category");
        }

        @Test
        @DisplayName("Should publish ProductCreatedEvent with correct data")
        void shouldPublishProductCreatedEventWithCorrectData() {
            // given
            CreateProductRequest request = new CreateProductRequest(
                    "Event Product", "Description", new BigDecimal("49.99"),
                    null, 10, "SKU-EVT", categoryId, "Brand", null
            );

            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
                Product saved = invocation.getArgument(0);
                saved.setId(productId);
                saved.setCreatedAt(LocalDateTime.now());
                saved.setUpdatedAt(LocalDateTime.now());
                return saved;
            });

            ArgumentCaptor<ProductCreatedEvent> eventCaptor = ArgumentCaptor.forClass(ProductCreatedEvent.class);

            // when
            productService.createProduct(sellerId, request);

            // then
            verify(productEventProducer).publishProductCreated(eventCaptor.capture());
            ProductCreatedEvent capturedEvent = eventCaptor.getValue();
            assertThat(capturedEvent.productId()).isEqualTo(productId);
            assertThat(capturedEvent.sellerId()).isEqualTo(sellerId);
            assertThat(capturedEvent.name()).isEqualTo("Event Product");
            assertThat(capturedEvent.price()).isEqualByComparingTo(new BigDecimal("49.99"));
            assertThat(capturedEvent.categorySlug()).isEqualTo("electronics");
        }
    }

    @Nested
    @DisplayName("updateProduct")
    class UpdateProduct {

        @Test
        @DisplayName("Should update product successfully")
        void shouldUpdateProductSuccessfully() {
            // given
            UpdateProductRequest request = new UpdateProductRequest(
                    "Updated Name", "Updated Desc", new BigDecimal("199.99"),
                    new BigDecimal("149.99"), 200, "SKU-UPD", null, "UpdatedBrand",
                    null, null
            );

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            ProductResponse result = productService.updateProduct(sellerId, productId, request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("Updated Name");
            assertThat(result.description()).isEqualTo("Updated Desc");
            assertThat(result.price()).isEqualByComparingTo(new BigDecimal("199.99"));
            assertThat(result.brand()).isEqualTo("UpdatedBrand");
            verify(productEventProducer).publishProductUpdated(any(ProductUpdatedEvent.class));
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when seller is not the owner")
        void shouldThrowBusinessRuleExceptionWhenWrongSeller() {
            // given
            UUID wrongSellerId = UUID.randomUUID();
            UpdateProductRequest request = new UpdateProductRequest(
                    "Updated Name", null, null, null, null, null, null, null, null, null
            );

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));

            // when / then
            assertThatThrownBy(() -> productService.updateProduct(wrongSellerId, productId, request))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("not authorized");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when product not found")
        void shouldThrowResourceNotFoundExceptionWhenProductNotFound() {
            // given
            UUID nonExistentId = UUID.randomUUID();
            UpdateProductRequest request = new UpdateProductRequest(
                    "Updated Name", null, null, null, null, null, null, null, null, null
            );

            when(productRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> productService.updateProduct(sellerId, nonExistentId, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Should update product with new category")
        void shouldUpdateProductWithNewCategory() {
            // given
            UUID newCategoryId = UUID.randomUUID();
            Category newCategory = Category.builder()
                    .name("Clothing")
                    .slug("clothing")
                    .level(1)
                    .isActive(true)
                    .children(new ArrayList<>())
                    .build();
            newCategory.setId(newCategoryId);
            newCategory.setCreatedAt(LocalDateTime.now());
            newCategory.setUpdatedAt(LocalDateTime.now());

            UpdateProductRequest request = new UpdateProductRequest(
                    null, null, null, null, null, null, newCategoryId, null, null, null
            );

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(categoryRepository.findById(newCategoryId)).thenReturn(Optional.of(newCategory));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            ProductResponse result = productService.updateProduct(sellerId, productId, request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.category().name()).isEqualTo("Clothing");
            verify(categoryRepository).findById(newCategoryId);
        }

        @Test
        @DisplayName("Should update product images by clearing old and adding new")
        void shouldUpdateProductImagesCorrectly() {
            // given
            List<ProductImageRequest> newImages = List.of(
                    new ProductImageRequest("http://new-image.jpg", "New Image", 0, true)
            );
            UpdateProductRequest request = new UpdateProductRequest(
                    null, null, null, null, null, null, null, null, null, newImages
            );

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            ProductResponse result = productService.updateProduct(sellerId, productId, request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.images()).hasSize(1);
            assertThat(result.images().get(0).url()).isEqualTo("http://new-image.jpg");
        }
    }

    @Nested
    @DisplayName("updateStock")
    class UpdateStock {

        @Test
        @DisplayName("Should set status to OUT_OF_STOCK when stock reaches 0")
        void shouldSetOutOfStockWhenStockReachesZero() {
            // given
            UpdateStockRequest request = new UpdateStockRequest(0);

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            ProductResponse result = productService.updateStock(sellerId, productId, request);

            // then
            assertThat(result.status()).isEqualTo(ProductStatus.OUT_OF_STOCK);
            assertThat(result.stockQuantity()).isEqualTo(0);
            verify(productEventProducer).publishProductStockUpdated(any(ProductStockUpdatedEvent.class));
        }

        @Test
        @DisplayName("Should set status to ACTIVE when replenishing OUT_OF_STOCK product")
        void shouldSetActiveWhenReplenishingOutOfStockProduct() {
            // given
            product.setStatus(ProductStatus.OUT_OF_STOCK);
            product.setStockQuantity(0);
            UpdateStockRequest request = new UpdateStockRequest(50);

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            ProductResponse result = productService.updateStock(sellerId, productId, request);

            // then
            assertThat(result.status()).isEqualTo(ProductStatus.ACTIVE);
            assertThat(result.stockQuantity()).isEqualTo(50);
        }

        @Test
        @DisplayName("Should not change status when updating stock on ACTIVE product with non-zero quantity")
        void shouldNotChangeStatusWhenUpdatingActiveProductStock() {
            // given
            UpdateStockRequest request = new UpdateStockRequest(25);

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            ProductResponse result = productService.updateStock(sellerId, productId, request);

            // then
            assertThat(result.status()).isEqualTo(ProductStatus.ACTIVE);
            assertThat(result.stockQuantity()).isEqualTo(25);
        }

        @Test
        @DisplayName("Should publish stock event when quantity is less than 10")
        void shouldPublishStockEventWhenQuantityLessThan10() {
            // given
            UpdateStockRequest request = new UpdateStockRequest(5);

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            productService.updateStock(sellerId, productId, request);

            // then
            verify(productEventProducer).publishProductStockUpdated(any(ProductStockUpdatedEvent.class));
        }

        @Test
        @DisplayName("Should not publish stock event when quantity is 10 or more")
        void shouldNotPublishStockEventWhenQuantity10OrMore() {
            // given
            UpdateStockRequest request = new UpdateStockRequest(50);

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            productService.updateStock(sellerId, productId, request);

            // then
            verify(productEventProducer, never()).publishProductStockUpdated(any(ProductStockUpdatedEvent.class));
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when seller is not the owner")
        void shouldThrowBusinessRuleExceptionWhenWrongSeller() {
            // given
            UUID wrongSellerId = UUID.randomUUID();
            UpdateStockRequest request = new UpdateStockRequest(50);

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));

            // when / then
            assertThatThrownBy(() -> productService.updateStock(wrongSellerId, productId, request))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("not authorized");
        }
    }

    @Nested
    @DisplayName("deleteProduct")
    class DeleteProduct {

        @Test
        @DisplayName("Should soft delete product by setting DELETED status")
        void shouldSoftDeleteProduct() {
            // given
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            productService.deleteProduct(sellerId, productId);

            // then
            ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
            verify(productRepository).save(productCaptor.capture());
            assertThat(productCaptor.getValue().getStatus()).isEqualTo(ProductStatus.DELETED);
            verify(productEventProducer).publishProductUpdated(any(ProductUpdatedEvent.class));
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when seller is not the owner")
        void shouldThrowBusinessRuleExceptionWhenWrongSeller() {
            // given
            UUID wrongSellerId = UUID.randomUUID();
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));

            // when / then
            assertThatThrownBy(() -> productService.deleteProduct(wrongSellerId, productId))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("not authorized");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when product not found")
        void shouldThrowResourceNotFoundExceptionWhenProductNotFound() {
            // given
            UUID nonExistentId = UUID.randomUUID();
            when(productRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> productService.deleteProduct(sellerId, nonExistentId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getSellerProducts")
    class GetSellerProducts {

        @Test
        @DisplayName("Should return seller products excluding deleted ones")
        void shouldReturnSellerProductsExcludingDeleted() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

            when(productRepository.findBySellerIdAndStatusNot(sellerId, ProductStatus.DELETED, pageable))
                    .thenReturn(productPage);

            // when
            PagedResponse<ProductResponse> result = productService.getSellerProducts(sellerId, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).sellerId()).isEqualTo(sellerId);
            verify(productRepository).findBySellerIdAndStatusNot(sellerId, ProductStatus.DELETED, pageable);
        }

        @Test
        @DisplayName("Should return empty page when seller has no products")
        void shouldReturnEmptyPageWhenSellerHasNoProducts() {
            // given
            UUID newSellerId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 20);
            Page<Product> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(productRepository.findBySellerIdAndStatusNot(newSellerId, ProductStatus.DELETED, pageable))
                    .thenReturn(emptyPage);

            // when
            PagedResponse<ProductResponse> result = productService.getSellerProducts(newSellerId, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("getProductsByCategory")
    class GetProductsByCategory {

        @Test
        @DisplayName("Should return products by category slug")
        void shouldReturnProductsByCategorySlug() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

            when(categoryRepository.findBySlug("electronics")).thenReturn(Optional.of(category));
            when(productRepository.findByCategoryAndStatus(category, ProductStatus.ACTIVE, pageable))
                    .thenReturn(productPage);

            // when
            PagedResponse<ProductResponse> result = productService.getProductsByCategory("electronics", pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when category slug not found")
        void shouldThrowResourceNotFoundExceptionWhenCategorySlugNotFound() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            when(categoryRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> productService.getProductsByCategory("nonexistent", pageable))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Category");
        }
    }
}
