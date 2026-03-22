package com.stardrop.service;

import com.stardrop.dto.CartItemRequest;
import com.stardrop.model.CartItem;
import com.stardrop.model.Product;
import com.stardrop.repository.CartItemRepository;
import com.stardrop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock private CartItemRepository cartItemRepository;
    @Mock private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L).name("Diamond").price(new BigDecimal("1875"))
                .stock(20).build();
    }

    @Test
    @DisplayName("addToCart - should add new item")
    void addToCart_newItem() {
        CartItemRequest request = new CartItemRequest(1L, 3);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByUserIdAndProductId(1L, 1L)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArgument(0));

        CartItem result = cartService.addToCart(1L, request);

        assertThat(result.getQuantity()).isEqualTo(3);
        assertThat(result.getProduct().getName()).isEqualTo("Diamond");
    }

    @Test
    @DisplayName("addToCart - should increase quantity if already in cart")
    void addToCart_existingItem() {
        CartItem existing = CartItem.builder()
                .id(1L).userId(1L).product(product).quantity(2).build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByUserIdAndProductId(1L, 1L)).thenReturn(Optional.of(existing));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArgument(0));

        CartItem result = cartService.addToCart(1L, new CartItemRequest(1L, 1));

        assertThat(result.getQuantity()).isEqualTo(3); // 2 + 1
    }

    @Test
    @DisplayName("addToCart - should throw when not enough stock")
    void addToCart_noStock() {
        product.setStock(1);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> cartService.addToCart(1L, new CartItemRequest(1L, 5)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Not enough stock");
    }

    @Test
    @DisplayName("getCart - should return user's cart items")
    void getCart_returnsList() {
        CartItem item = CartItem.builder().id(1L).userId(1L).product(product).quantity(1).build();

        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(item));

        List<CartItem> cart = cartService.getCart(1L);

        assertThat(cart).hasSize(1);
        assertThat(cart.get(0).getProduct().getName()).isEqualTo("Diamond");
    }

    @Test
    @DisplayName("removeFromCart - should throw if item belongs to different user")
    void removeFromCart_wrongUser() {
        CartItem item = CartItem.builder().id(1L).userId(2L).product(product).quantity(1).build();

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> cartService.removeFromCart(1L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("does not belong");
    }

    @Test
    @DisplayName("updateCartItem - should delete if quantity is 0")
    void updateCartItem_zeroQuantity() {
        CartItem item = CartItem.builder().id(1L).userId(1L).product(product).quantity(3).build();

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(item));

        CartItem result = cartService.updateCartItem(1L, 1L, 0);

        assertThat(result).isNull();
        verify(cartItemRepository).delete(item);
    }
}
