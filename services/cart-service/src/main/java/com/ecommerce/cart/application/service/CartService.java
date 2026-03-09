package com.ecommerce.cart.application.service;

import com.ecommerce.cart.application.dto.*;
import com.ecommerce.cart.domain.model.Cart;
import com.ecommerce.cart.domain.model.CartItem;
import com.ecommerce.cart.infrastructure.persistence.CartRedisRepository;
import com.ecommerce.common.exception.BusinessRuleException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private static final Logger log = LoggerFactory.getLogger(CartService.class);
    private static final int MAX_CART_ITEMS = 50;

    private final CartRedisRepository cartRedisRepository;

    public CartResponse getCart(String userId) {
        Cart cart = cartRedisRepository.findByUserId(userId);
        return toCartResponse(cart);
    }

    public CartResponse addItem(String userId, AddToCartRequest request) {
        Cart cart = cartRedisRepository.findByUserId(userId);

        if (cart.getItems().size() >= MAX_CART_ITEMS && !cart.getItems().containsKey(request.productId())) {
            throw new BusinessRuleException("Cart cannot contain more than " + MAX_CART_ITEMS + " items");
        }

        CartItem existingItem = cart.getItems().get(request.productId());
        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + request.quantity();
            if (newQuantity > 10) {
                throw new BusinessRuleException("Quantity for a single product cannot exceed 10");
            }
            existingItem.setQuantity(newQuantity);
            cartRedisRepository.addItem(userId, existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .productId(request.productId())
                    .productName("Product " + request.productId())
                    .price(BigDecimal.ZERO)
                    .quantity(request.quantity())
                    .imageUrl(null)
                    .sellerId(null)
                    .addedAt(LocalDateTime.now())
                    .priceChanged(false)
                    .currentPrice(BigDecimal.ZERO)
                    .available(true)
                    .build();
            cartRedisRepository.addItem(userId, newItem);
        }

        log.info("Added item productId={} to cart for userId={}", request.productId(), userId);

        Cart updatedCart = cartRedisRepository.findByUserId(userId);
        return toCartResponse(updatedCart);
    }

    public CartResponse updateQuantity(String userId, String productId, UpdateQuantityRequest request) {
        if (!cartRedisRepository.existsItem(userId, productId)) {
            throw new ResourceNotFoundException("CartItem", "productId", productId);
        }

        cartRedisRepository.updateItemQuantity(userId, productId, request.quantity());
        log.info("Updated quantity for productId={} to {} in cart for userId={}", productId, request.quantity(), userId);

        Cart updatedCart = cartRedisRepository.findByUserId(userId);
        return toCartResponse(updatedCart);
    }

    public CartResponse removeItem(String userId, String productId) {
        if (!cartRedisRepository.existsItem(userId, productId)) {
            throw new ResourceNotFoundException("CartItem", "productId", productId);
        }

        cartRedisRepository.removeItem(userId, productId);
        log.info("Removed item productId={} from cart for userId={}", productId, userId);

        Cart updatedCart = cartRedisRepository.findByUserId(userId);
        return toCartResponse(updatedCart);
    }

    public void clearCart(String userId) {
        cartRedisRepository.clearCart(userId);
        log.info("Cleared cart for userId={}", userId);
    }

    public CartSummaryResponse getSummary(String userId) {
        Cart cart = cartRedisRepository.findByUserId(userId);
        String estimatedDelivery = "2-4 business days";
        return new CartSummaryResponse(
                cart.getTotalAmount(),
                cart.getItemCount(),
                estimatedDelivery
        );
    }

    private CartResponse toCartResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().values().stream()
                .map(this::toCartItemResponse)
                .toList();

        return new CartResponse(
                cart.getUserId(),
                items,
                cart.getTotalAmount(),
                cart.getItemCount()
        );
    }

    private CartItemResponse toCartItemResponse(CartItem item) {
        return new CartItemResponse(
                item.getProductId(),
                item.getProductName(),
                item.getPrice(),
                item.getQuantity(),
                item.getImageUrl(),
                item.getSellerId(),
                item.getAddedAt(),
                item.isPriceChanged(),
                item.getCurrentPrice(),
                item.isAvailable()
        );
    }
}
