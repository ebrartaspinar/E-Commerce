package com.ecommerce.cart.api.controller;

import com.ecommerce.cart.application.dto.*;
import com.ecommerce.cart.application.service.CartService;
import com.ecommerce.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping cart management endpoints")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get current user's cart")
    public ResponseEntity<ApiResponse<CartResponse>> getCart() {
        String userId = getAuthenticatedUserId().toString();
        CartResponse response = cartService.getCart(userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(@Valid @RequestBody AddToCartRequest request) {
        String userId = getAuthenticatedUserId().toString();
        CartResponse response = cartService.addItem(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response, "Item added to cart"));
    }

    @PutMapping("/items/{productId}")
    @Operation(summary = "Update item quantity in cart")
    public ResponseEntity<ApiResponse<CartResponse>> updateQuantity(
            @PathVariable String productId,
            @Valid @RequestBody UpdateQuantityRequest request) {
        String userId = getAuthenticatedUserId().toString();
        CartResponse response = cartService.updateQuantity(userId, productId, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Quantity updated"));
    }

    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(@PathVariable String productId) {
        String userId = getAuthenticatedUserId().toString();
        CartResponse response = cartService.removeItem(userId, productId);
        return ResponseEntity.ok(ApiResponse.ok(response, "Item removed from cart"));
    }

    @DeleteMapping
    @Operation(summary = "Clear entire cart")
    public ResponseEntity<ApiResponse<Void>> clearCart() {
        String userId = getAuthenticatedUserId().toString();
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.ok(null, "Cart cleared"));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get cart summary")
    public ResponseEntity<ApiResponse<CartSummaryResponse>> getSummary() {
        String userId = getAuthenticatedUserId().toString();
        CartSummaryResponse response = cartService.getSummary(userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UUID) authentication.getPrincipal();
    }
}
