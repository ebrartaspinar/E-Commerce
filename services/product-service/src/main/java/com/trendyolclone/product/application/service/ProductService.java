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
import com.trendyolclone.product.infrastructure.persistence.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductEventProducer productEventProducer;

    public PagedResponse<ProductResponse> getProducts(ProductFilterParams filterParams, Pageable pageable) {
        Specification<Product> spec = Specification.where(ProductSpecification.isNotDeleted());

        if (filterParams.categoryId() != null) {
            spec = spec.and(ProductSpecification.hasCategoryId(filterParams.categoryId()));
        }
        if (filterParams.brand() != null && !filterParams.brand().isBlank()) {
            spec = spec.and(ProductSpecification.hasBrand(filterParams.brand()));
        }
        if (filterParams.minPrice() != null || filterParams.maxPrice() != null) {
            spec = spec.and(ProductSpecification.hasPriceRange(filterParams.minPrice(), filterParams.maxPrice()));
        }
        if (filterParams.status() != null) {
            spec = spec.and(ProductSpecification.hasStatus(filterParams.status()));
        }
        if (filterParams.search() != null && !filterParams.search().isBlank()) {
            spec = spec.and(ProductSpecification.hasSearchTerm(filterParams.search()));
        }

        Page<ProductResponse> page = productRepository.findAll(spec, pageable).map(this::mapToResponse);
        return PagedResponse.from(page);
    }

    @Cacheable(value = "products", key = "#id")
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return mapToResponse(product);
    }

    @Cacheable(value = "products", key = "'byCategory:' + #categorySlug + ':page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public PagedResponse<ProductResponse> getProductsByCategory(String categorySlug, Pageable pageable) {
        Category category = categoryRepository.findBySlug(categorySlug)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", categorySlug));

        Page<ProductResponse> page = productRepository
                .findByCategoryAndStatus(category, ProductStatus.ACTIVE, pageable)
                .map(this::mapToResponse);
        return PagedResponse.from(page);
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse createProduct(UUID sellerId, CreateProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.categoryId()));

        Product product = Product.builder()
                .sellerId(sellerId)
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .discountedPrice(request.discountedPrice())
                .stockQuantity(request.stockQuantity())
                .sku(request.sku())
                .status(ProductStatus.ACTIVE)
                .category(category)
                .brand(request.brand())
                .averageRating(0.0)
                .reviewCount(0)
                .images(new ArrayList<>())
                .build();

        if (request.images() != null && !request.images().isEmpty()) {
            for (ProductImageRequest imageRequest : request.images()) {
                ProductImage image = ProductImage.builder()
                        .url(imageRequest.url())
                        .altText(imageRequest.altText())
                        .sortOrder(imageRequest.sortOrder() != null ? imageRequest.sortOrder() : 0)
                        .isMain(imageRequest.isMain())
                        .build();
                product.addImage(image);
            }
        }

        Product savedProduct = productRepository.save(product);
        log.info("Product created: id={}, name={}, sellerId={}", savedProduct.getId(), savedProduct.getName(), sellerId);

        ProductCreatedEvent event = new ProductCreatedEvent(
                savedProduct.getId(),
                sellerId,
                savedProduct.getName(),
                savedProduct.getPrice(),
                category.getSlug()
        );
        productEventProducer.publishProductCreated(event);

        return mapToResponse(savedProduct);
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse updateProduct(UUID sellerId, UUID productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (!product.getSellerId().equals(sellerId)) {
            throw new BusinessRuleException("You are not authorized to update this product");
        }

        if (request.name() != null) {
            product.setName(request.name());
        }
        if (request.description() != null) {
            product.setDescription(request.description());
        }
        if (request.price() != null) {
            product.setPrice(request.price());
        }
        if (request.discountedPrice() != null) {
            product.setDiscountedPrice(request.discountedPrice());
        }
        if (request.stockQuantity() != null) {
            product.setStockQuantity(request.stockQuantity());
        }
        if (request.sku() != null) {
            product.setSku(request.sku());
        }
        if (request.status() != null) {
            product.setStatus(request.status());
        }
        if (request.brand() != null) {
            product.setBrand(request.brand());
        }
        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.categoryId()));
            product.setCategory(category);
        }
        if (request.images() != null) {
            product.clearImages();
            for (ProductImageRequest imageRequest : request.images()) {
                ProductImage image = ProductImage.builder()
                        .url(imageRequest.url())
                        .altText(imageRequest.altText())
                        .sortOrder(imageRequest.sortOrder() != null ? imageRequest.sortOrder() : 0)
                        .isMain(imageRequest.isMain())
                        .build();
                product.addImage(image);
            }
        }

        Product savedProduct = productRepository.save(product);
        log.info("Product updated: id={}, sellerId={}", productId, sellerId);

        ProductUpdatedEvent event = new ProductUpdatedEvent(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getPrice(),
                savedProduct.getStockQuantity()
        );
        productEventProducer.publishProductUpdated(event);

        return mapToResponse(savedProduct);
    }

    @Transactional
    @CacheEvict(value = "products", key = "#productId")
    public ProductResponse updateStock(UUID sellerId, UUID productId, UpdateStockRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (!product.getSellerId().equals(sellerId)) {
            throw new BusinessRuleException("You are not authorized to update this product's stock");
        }

        product.setStockQuantity(request.quantity());

        if (request.quantity() == 0) {
            product.setStatus(ProductStatus.OUT_OF_STOCK);
        } else if (product.getStatus() == ProductStatus.OUT_OF_STOCK) {
            product.setStatus(ProductStatus.ACTIVE);
        }

        Product savedProduct = productRepository.save(product);
        log.info("Product stock updated: id={}, newStock={}", productId, request.quantity());

        if (request.quantity() < 10) {
            ProductStockUpdatedEvent event = new ProductStockUpdatedEvent(
                    savedProduct.getId(),
                    sellerId,
                    savedProduct.getStockQuantity(),
                    savedProduct.getName()
            );
            productEventProducer.publishProductStockUpdated(event);
        }

        return mapToResponse(savedProduct);
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(UUID sellerId, UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (!product.getSellerId().equals(sellerId)) {
            throw new BusinessRuleException("You are not authorized to delete this product");
        }

        product.setStatus(ProductStatus.DELETED);
        productRepository.save(product);
        log.info("Product soft-deleted: id={}, sellerId={}", productId, sellerId);

        ProductUpdatedEvent event = new ProductUpdatedEvent(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStockQuantity()
        );
        productEventProducer.publishProductUpdated(event);
    }

    public PagedResponse<ProductResponse> getSellerProducts(UUID sellerId, Pageable pageable) {
        Page<ProductResponse> page = productRepository
                .findBySellerIdAndStatusNot(sellerId, ProductStatus.DELETED, pageable)
                .map(this::mapToResponse);
        return PagedResponse.from(page);
    }

    private ProductResponse mapToResponse(Product product) {
        CategoryResponse categoryResponse = null;
        if (product.getCategory() != null) {
            Category category = product.getCategory();
            categoryResponse = new CategoryResponse(
                    category.getId(),
                    category.getName(),
                    category.getSlug(),
                    category.getParentCategory() != null ? category.getParentCategory().getId() : null,
                    category.getLevel(),
                    category.isActive(),
                    Collections.emptyList()
            );
        }

        List<ProductImageResponse> imageResponses = product.getImages() != null
                ? product.getImages().stream()
                    .map(image -> new ProductImageResponse(
                            image.getId(),
                            image.getUrl(),
                            image.getAltText(),
                            image.getSortOrder(),
                            image.isMain()
                    ))
                    .toList()
                : Collections.emptyList();

        return new ProductResponse(
                product.getId(),
                product.getSellerId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getDiscountedPrice(),
                product.getStockQuantity(),
                product.getSku(),
                product.getStatus(),
                categoryResponse,
                product.getBrand(),
                imageResponses,
                product.getAverageRating(),
                product.getReviewCount(),
                product.getCreatedAt()
        );
    }
}
