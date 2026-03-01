package com.trendyolclone.product.api.controller;

import com.trendyolclone.common.dto.ApiResponse;
import com.trendyolclone.common.dto.PagedResponse;
import com.trendyolclone.product.application.dto.*;
import com.trendyolclone.product.application.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seller/products")
@PreAuthorize("hasRole('SELLER')")
@RequiredArgsConstructor
@Tag(name = "Seller Products", description = "Seller product management endpoints")
public class SellerProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get seller's products")
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponse>>> getSellerProducts(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        UUID sellerId = getAuthenticatedUserId();
        PagedResponse<ProductResponse> products = productService.getSellerProducts(sellerId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request
    ) {
        UUID sellerId = getAuthenticatedUserId();
        ProductResponse product = productService.createProduct(sellerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(product, "Product created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        UUID sellerId = getAuthenticatedUserId();
        ProductResponse product = productService.updateProduct(sellerId, id, request);
        return ResponseEntity.ok(ApiResponse.ok(product, "Product updated successfully"));
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update product stock")
    public ResponseEntity<ApiResponse<ProductResponse>> updateStock(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStockRequest request
    ) {
        UUID sellerId = getAuthenticatedUserId();
        ProductResponse product = productService.updateStock(sellerId, id, request);
        return ResponseEntity.ok(ApiResponse.ok(product, "Stock updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID id) {
        UUID sellerId = getAuthenticatedUserId();
        productService.deleteProduct(sellerId, id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Product deleted successfully"));
    }

    private UUID getAuthenticatedUserId() {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        return UUID.fromString(userIdStr);
    }
}
