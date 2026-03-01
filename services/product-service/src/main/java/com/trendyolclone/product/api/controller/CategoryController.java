package com.trendyolclone.product.api.controller;

import com.trendyolclone.common.dto.ApiResponse;
import com.trendyolclone.common.dto.PagedResponse;
import com.trendyolclone.product.application.dto.CategoryResponse;
import com.trendyolclone.product.application.dto.ProductResponse;
import com.trendyolclone.product.application.service.CategoryService;
import com.trendyolclone.product.application.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category browsing endpoints")
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get category tree")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategoryTree() {
        List<CategoryResponse> categories = categoryService.getCategoryTree();
        return ResponseEntity.ok(ApiResponse.ok(categories));
    }

    @GetMapping("/{slug}/products")
    @Operation(summary = "Get products by category slug")
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponse>>> getProductsByCategory(
            @PathVariable String slug,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PagedResponse<ProductResponse> products = productService.getProductsByCategory(slug, pageable);
        return ResponseEntity.ok(ApiResponse.ok(products));
    }
}
