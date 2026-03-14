package com.stardrop.service;

import com.stardrop.dto.CartItemRequest;
import com.stardrop.model.CartItem;
import com.stardrop.model.Product;
import com.stardrop.repository.CartItemRepository;
import com.stardrop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public List<CartItem> getCart(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }

    @Transactional
    public CartItem addToCart(Long userId, CartItemRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.productId()));

        if (product.getStock() < request.quantity()) {
            throw new RuntimeException("Not enough stock. Available: " + product.getStock());
        }

        Optional<CartItem> existing = cartItemRepository.findByUserIdAndProductId(userId, request.productId());

        if (existing.isPresent()) {
            CartItem cartItem = existing.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.quantity());
            return cartItemRepository.save(cartItem);
        }

        CartItem cartItem = CartItem.builder()
                .userId(userId)
                .product(product)
                .quantity(request.quantity())
                .build();

        return cartItemRepository.save(cartItem);
    }

    @Transactional
    public CartItem updateCartItem(Long userId, Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cartItem.getUserId().equals(userId)) {
            throw new RuntimeException("This cart item does not belong to you");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            return null;
        }

        if (cartItem.getProduct().getStock() < quantity) {
            throw new RuntimeException("Not enough stock. Available: " + cartItem.getProduct().getStock());
        }

        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    @Transactional
    public void removeFromCart(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cartItem.getUserId().equals(userId)) {
            throw new RuntimeException("This cart item does not belong to you");
        }

        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}
