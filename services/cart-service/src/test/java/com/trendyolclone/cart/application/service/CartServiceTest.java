package com.trendyolclone.cart.application.service;

import com.trendyolclone.cart.application.dto.*;
import com.trendyolclone.cart.domain.model.Cart;
import com.trendyolclone.cart.domain.model.CartItem;
import com.trendyolclone.cart.infrastructure.persistence.CartRedisRepository;
import com.trendyolclone.common.exception.BusinessRuleException;
import com.trendyolclone.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartService Unit Tests")
class CartServiceTest {

    @Mock
    private CartRedisRepository cartRedisRepository;

    @InjectMocks
    private CartService cartService;

    private String userId;
    private Cart emptyCart;
    private Cart cartWithItems;
    private CartItem existingItem;

    @BeforeEach
    void setUp() {
        userId = "user-123";

        emptyCart = Cart.builder()
                .userId(userId)
                .items(new HashMap<>())
                .build();

        existingItem = CartItem.builder()
                .productId("product-1")
                .productName("Product One")
                .price(new BigDecimal("29.99"))
                .quantity(2)
                .imageUrl("http://img.jpg")
                .sellerId("seller-1")
                .addedAt(LocalDateTime.now())
                .priceChanged(false)
                .currentPrice(new BigDecimal("29.99"))
                .available(true)
                .build();

        Map<String, CartItem> items = new HashMap<>();
        items.put("product-1", existingItem);

        cartWithItems = Cart.builder()
                .userId(userId)
                .items(items)
                .build();
    }

    @Nested
    @DisplayName("getCart")
    class GetCart {

        @Test
        @DisplayName("Should return cart with items when cart exists")
        void shouldReturnCartWithItemsWhenCartExists() {
            // given
            when(cartRedisRepository.findByUserId(userId)).thenReturn(cartWithItems);

            // when
            CartResponse result = cartService.getCart(userId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.items()).hasSize(1);
            assertThat(result.items().get(0).productId()).isEqualTo("product-1");
            assertThat(result.items().get(0).productName()).isEqualTo("Product One");
            assertThat(result.items().get(0).price()).isEqualByComparingTo(new BigDecimal("29.99"));
            assertThat(result.items().get(0).quantity()).isEqualTo(2);
            assertThat(result.itemCount()).isEqualTo(2);
            assertThat(result.totalAmount()).isEqualByComparingTo(new BigDecimal("59.98"));
            verify(cartRedisRepository).findByUserId(userId);
        }

        @Test
        @DisplayName("Should return empty cart when no cart found for user")
        void shouldReturnEmptyCartWhenNotFound() {
            // given
            when(cartRedisRepository.findByUserId(userId)).thenReturn(emptyCart);

            // when
            CartResponse result = cartService.getCart(userId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.items()).isEmpty();
            assertThat(result.itemCount()).isEqualTo(0);
            assertThat(result.totalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("addItem")
    class AddItem {

        @Test
        @DisplayName("Should add new item to cart")
        void shouldAddNewItemToCart() {
            // given
            AddToCartRequest request = new AddToCartRequest("product-2", 1);

            when(cartRedisRepository.findByUserId(userId))
                    .thenReturn(emptyCart)
                    .thenReturn(emptyCart);

            // when
            CartResponse result = cartService.addItem(userId, request);

            // then
            assertThat(result).isNotNull();
            verify(cartRedisRepository).addItem(eq(userId), any(CartItem.class));
        }

        @Test
        @DisplayName("Should stack quantity when adding existing item")
        void shouldStackQuantityWhenAddingExistingItem() {
            // given
            AddToCartRequest request = new AddToCartRequest("product-1", 3);

            when(cartRedisRepository.findByUserId(userId))
                    .thenReturn(cartWithItems)
                    .thenReturn(cartWithItems);

            // when
            CartResponse result = cartService.addItem(userId, request);

            // then
            assertThat(result).isNotNull();
            verify(cartRedisRepository).addItem(eq(userId), any(CartItem.class));
            // The existing item had quantity 2, adding 3 = 5
            assertThat(existingItem.getQuantity()).isEqualTo(5);
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when quantity exceeds 10 for single product")
        void shouldThrowBusinessRuleExceptionWhenQuantityExceeds10() {
            // given
            AddToCartRequest request = new AddToCartRequest("product-1", 9);

            when(cartRedisRepository.findByUserId(userId)).thenReturn(cartWithItems);

            // when / then
            // existing item has quantity 2, adding 9 = 11 > 10
            assertThatThrownBy(() -> cartService.addItem(userId, request))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("cannot exceed 10");
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when cart reaches 50 items limit")
        void shouldThrowBusinessRuleExceptionWhenCartReaches50ItemsLimit() {
            // given
            Map<String, CartItem> fullItems = new HashMap<>();
            for (int i = 0; i < 50; i++) {
                String pid = "product-" + i;
                fullItems.put(pid, CartItem.builder()
                        .productId(pid)
                        .productName("Product " + i)
                        .price(new BigDecimal("10.00"))
                        .quantity(1)
                        .available(true)
                        .addedAt(LocalDateTime.now())
                        .build());
            }

            Cart fullCart = Cart.builder()
                    .userId(userId)
                    .items(fullItems)
                    .build();

            AddToCartRequest request = new AddToCartRequest("new-product", 1);

            when(cartRedisRepository.findByUserId(userId)).thenReturn(fullCart);

            // when / then
            assertThatThrownBy(() -> cartService.addItem(userId, request))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("50");
        }

        @Test
        @DisplayName("Should allow adding to existing item even when cart has 50 items")
        void shouldAllowAddingToExistingItemWhenCartHas50Items() {
            // given
            Map<String, CartItem> fullItems = new HashMap<>();
            for (int i = 0; i < 50; i++) {
                String pid = "product-" + i;
                fullItems.put(pid, CartItem.builder()
                        .productId(pid)
                        .productName("Product " + i)
                        .price(new BigDecimal("10.00"))
                        .quantity(1)
                        .available(true)
                        .addedAt(LocalDateTime.now())
                        .build());
            }

            Cart fullCart = Cart.builder()
                    .userId(userId)
                    .items(fullItems)
                    .build();

            // Adding to an existing item (product-0 is already in the cart)
            AddToCartRequest request = new AddToCartRequest("product-0", 1);

            when(cartRedisRepository.findByUserId(userId))
                    .thenReturn(fullCart)
                    .thenReturn(fullCart);

            // when
            CartResponse result = cartService.addItem(userId, request);

            // then
            assertThat(result).isNotNull();
            verify(cartRedisRepository).addItem(eq(userId), any(CartItem.class));
        }
    }

    @Nested
    @DisplayName("updateQuantity")
    class UpdateQuantity {

        @Test
        @DisplayName("Should update item quantity successfully")
        void shouldUpdateItemQuantitySuccessfully() {
            // given
            UpdateQuantityRequest request = new UpdateQuantityRequest(5);

            when(cartRedisRepository.existsItem(userId, "product-1")).thenReturn(true);
            when(cartRedisRepository.findByUserId(userId)).thenReturn(cartWithItems);

            // when
            CartResponse result = cartService.updateQuantity(userId, "product-1", request);

            // then
            assertThat(result).isNotNull();
            verify(cartRedisRepository).updateItemQuantity(userId, "product-1", 5);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when item not found in cart")
        void shouldThrowResourceNotFoundExceptionWhenItemNotFound() {
            // given
            UpdateQuantityRequest request = new UpdateQuantityRequest(3);

            when(cartRedisRepository.existsItem(userId, "nonexistent")).thenReturn(false);

            // when / then
            assertThatThrownBy(() -> cartService.updateQuantity(userId, "nonexistent", request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("CartItem");
        }
    }

    @Nested
    @DisplayName("removeItem")
    class RemoveItem {

        @Test
        @DisplayName("Should remove item from cart successfully")
        void shouldRemoveItemFromCartSuccessfully() {
            // given
            when(cartRedisRepository.existsItem(userId, "product-1")).thenReturn(true);
            when(cartRedisRepository.findByUserId(userId)).thenReturn(emptyCart);

            // when
            CartResponse result = cartService.removeItem(userId, "product-1");

            // then
            assertThat(result).isNotNull();
            verify(cartRedisRepository).removeItem(userId, "product-1");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when removing nonexistent item")
        void shouldThrowResourceNotFoundExceptionWhenRemovingNonexistentItem() {
            // given
            when(cartRedisRepository.existsItem(userId, "nonexistent")).thenReturn(false);

            // when / then
            assertThatThrownBy(() -> cartService.removeItem(userId, "nonexistent"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("CartItem");
        }
    }

    @Nested
    @DisplayName("clearCart")
    class ClearCart {

        @Test
        @DisplayName("Should clear cart successfully")
        void shouldClearCartSuccessfully() {
            // when
            cartService.clearCart(userId);

            // then
            verify(cartRedisRepository).clearCart(userId);
        }
    }

    @Nested
    @DisplayName("getSummary")
    class GetSummary {

        @Test
        @DisplayName("Should return cart summary with total and item count")
        void shouldReturnCartSummaryWithTotalAndItemCount() {
            // given
            when(cartRedisRepository.findByUserId(userId)).thenReturn(cartWithItems);

            // when
            CartSummaryResponse result = cartService.getSummary(userId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.totalAmount()).isEqualByComparingTo(new BigDecimal("59.98"));
            assertThat(result.itemCount()).isEqualTo(2);
            assertThat(result.estimatedDelivery()).isEqualTo("2-4 business days");
        }

        @Test
        @DisplayName("Should return zero summary for empty cart")
        void shouldReturnZeroSummaryForEmptyCart() {
            // given
            when(cartRedisRepository.findByUserId(userId)).thenReturn(emptyCart);

            // when
            CartSummaryResponse result = cartService.getSummary(userId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.totalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(result.itemCount()).isEqualTo(0);
            assertThat(result.estimatedDelivery()).isEqualTo("2-4 business days");
        }

        @Test
        @DisplayName("Should calculate total correctly with multiple items")
        void shouldCalculateTotalCorrectlyWithMultipleItems() {
            // given
            Map<String, CartItem> items = new HashMap<>();
            items.put("product-1", CartItem.builder()
                    .productId("product-1")
                    .productName("Product One")
                    .price(new BigDecimal("10.00"))
                    .quantity(3)
                    .available(true)
                    .addedAt(LocalDateTime.now())
                    .build());
            items.put("product-2", CartItem.builder()
                    .productId("product-2")
                    .productName("Product Two")
                    .price(new BigDecimal("25.50"))
                    .quantity(2)
                    .available(true)
                    .addedAt(LocalDateTime.now())
                    .build());

            Cart multiItemCart = Cart.builder()
                    .userId(userId)
                    .items(items)
                    .build();

            when(cartRedisRepository.findByUserId(userId)).thenReturn(multiItemCart);

            // when
            CartSummaryResponse result = cartService.getSummary(userId);

            // then
            // 10.00 * 3 = 30.00 + 25.50 * 2 = 51.00 => total = 81.00
            assertThat(result.totalAmount()).isEqualByComparingTo(new BigDecimal("81.00"));
            assertThat(result.itemCount()).isEqualTo(5);
        }
    }
}
