package com.ecommerce.cart.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ecommerce.cart.domain.model.Cart;
import com.ecommerce.cart.domain.model.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartRedisRepository Unit Tests")
class CartRedisRepositoryTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    private ObjectMapper objectMapper;
    private CartRedisRepository cartRedisRepository;

    private String userId;
    private String cartKey;
    private CartItem sampleItem;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        cartRedisRepository = new CartRedisRepository(redisTemplate, objectMapper);

        userId = "user-123";
        cartKey = "cart:" + userId;

        sampleItem = CartItem.builder()
                .productId("product-1")
                .productName("Test Product")
                .price(new BigDecimal("29.99"))
                .quantity(2)
                .imageUrl("http://img.jpg")
                .sellerId("seller-1")
                .addedAt(LocalDateTime.of(2025, 1, 15, 10, 30))
                .priceChanged(false)
                .currentPrice(new BigDecimal("29.99"))
                .available(true)
                .build();
    }

    @Nested
    @DisplayName("findByUserId")
    class FindByUserId {

        @Test
        @DisplayName("Should return cart with items when entries exist in Redis")
        void shouldReturnCartWithItemsWhenEntriesExist() throws JsonProcessingException {
            // given
            String serializedItem = objectMapper.writeValueAsString(sampleItem);
            Map<Object, Object> entries = new HashMap<>();
            entries.put("product-1", serializedItem);

            when(redisTemplate.opsForHash()).thenReturn(hashOperations);
            when(hashOperations.entries(cartKey)).thenReturn(entries);

            // when
            Cart result = cartRedisRepository.findByUserId(userId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(userId);
            assertThat(result.getItems()).hasSize(1);
            assertThat(result.getItems()).containsKey("product-1");

            CartItem item = result.getItems().get("product-1");
            assertThat(item.getProductId()).isEqualTo("product-1");
            assertThat(item.getProductName()).isEqualTo("Test Product");
            assertThat(item.getPrice()).isEqualByComparingTo(new BigDecimal("29.99"));
            assertThat(item.getQuantity()).isEqualTo(2);

            verify(redisTemplate).opsForHash();
            verify(hashOperations).entries(cartKey);
        }

        @Test
        @DisplayName("Should return empty cart when no entries exist in Redis")
        void shouldReturnEmptyCartWhenNoEntriesExist() {
            // given
            when(redisTemplate.opsForHash()).thenReturn(hashOperations);
            when(hashOperations.entries(cartKey)).thenReturn(new HashMap<>());

            // when
            Cart result = cartRedisRepository.findByUserId(userId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(userId);
            assertThat(result.getItems()).isEmpty();
        }

        @Test
        @DisplayName("Should handle multiple items in cart")
        void shouldHandleMultipleItemsInCart() throws JsonProcessingException {
            // given
            CartItem item2 = CartItem.builder()
                    .productId("product-2")
                    .productName("Another Product")
                    .price(new BigDecimal("49.99"))
                    .quantity(1)
                    .available(true)
                    .addedAt(LocalDateTime.of(2025, 1, 16, 14, 0))
                    .build();

            Map<Object, Object> entries = new HashMap<>();
            entries.put("product-1", objectMapper.writeValueAsString(sampleItem));
            entries.put("product-2", objectMapper.writeValueAsString(item2));

            when(redisTemplate.opsForHash()).thenReturn(hashOperations);
            when(hashOperations.entries(cartKey)).thenReturn(entries);

            // when
            Cart result = cartRedisRepository.findByUserId(userId);

            // then
            assertThat(result.getItems()).hasSize(2);
            assertThat(result.getItems()).containsKeys("product-1", "product-2");
        }

        @Test
        @DisplayName("Should skip items with invalid JSON gracefully")
        void shouldSkipItemsWithInvalidJsonGracefully() throws JsonProcessingException {
            // given
            Map<Object, Object> entries = new HashMap<>();
            entries.put("product-1", objectMapper.writeValueAsString(sampleItem));
            entries.put("product-bad", "invalid-json{{{");

            when(redisTemplate.opsForHash()).thenReturn(hashOperations);
            when(hashOperations.entries(cartKey)).thenReturn(entries);

            // when
            Cart result = cartRedisRepository.findByUserId(userId);

            // then
            assertThat(result.getItems()).hasSize(1);
            assertThat(result.getItems()).containsKey("product-1");
        }
    }

    @Nested
    @DisplayName("addItem")
    class AddItem {

        @Test
        @DisplayName("Should add item to Redis hash and set TTL")
        void shouldAddItemToRedisHashAndSetTtl() {
            // given
            when(redisTemplate.opsForHash()).thenReturn(hashOperations);

            // when
            cartRedisRepository.addItem(userId, sampleItem);

            // then
            verify(hashOperations).put(eq(cartKey), eq("product-1"), anyString());
            verify(redisTemplate).expire(cartKey, 30, TimeUnit.DAYS);
        }

        @Test
        @DisplayName("Should serialize cart item correctly for Redis storage")
        void shouldSerializeCartItemCorrectly() throws JsonProcessingException {
            // given
            when(redisTemplate.opsForHash()).thenReturn(hashOperations);

            // when
            cartRedisRepository.addItem(userId, sampleItem);

            // then
            verify(hashOperations).put(eq(cartKey), eq("product-1"), argThat(json -> {
                try {
                    CartItem deserialized = objectMapper.readValue((String) json, CartItem.class);
                    return deserialized.getProductId().equals("product-1")
                            && deserialized.getQuantity() == 2
                            && deserialized.getPrice().compareTo(new BigDecimal("29.99")) == 0;
                } catch (JsonProcessingException e) {
                    return false;
                }
            }));
        }

        @Test
        @DisplayName("Should use correct Redis key pattern cart:{userId}")
        void shouldUseCorrectRedisKeyPattern() {
            // given
            when(redisTemplate.opsForHash()).thenReturn(hashOperations);
            String testUserId = "test-user-456";

            // when
            cartRedisRepository.addItem(testUserId, sampleItem);

            // then
            verify(hashOperations).put(eq("cart:test-user-456"), eq("product-1"), anyString());
        }
    }

    @Nested
    @DisplayName("updateItemQuantity")
    class UpdateItemQuantity {

        @Test
        @DisplayName("Should update item quantity in Redis")
        void shouldUpdateItemQuantityInRedis() throws JsonProcessingException {
            // given
            String serializedItem = objectMapper.writeValueAsString(sampleItem);

            when(redisTemplate.opsForHash()).thenReturn(hashOperations);
            when(hashOperations.get(cartKey, "product-1")).thenReturn(serializedItem);

            // when
            cartRedisRepository.updateItemQuantity(userId, "product-1", 5);

            // then
            verify(hashOperations).put(eq(cartKey), eq("product-1"), argThat(json -> {
                try {
                    CartItem deserialized = objectMapper.readValue((String) json, CartItem.class);
                    return deserialized.getQuantity() == 5;
                } catch (JsonProcessingException e) {
                    return false;
                }
            }));
            verify(redisTemplate).expire(cartKey, 30, TimeUnit.DAYS);
        }

        @Test
        @DisplayName("Should do nothing when item not found in Redis")
        void shouldDoNothingWhenItemNotFoundInRedis() {
            // given
            when(redisTemplate.opsForHash()).thenReturn(hashOperations);
            when(hashOperations.get(cartKey, "nonexistent")).thenReturn(null);

            // when
            cartRedisRepository.updateItemQuantity(userId, "nonexistent", 3);

            // then
            verify(hashOperations, never()).put(eq(cartKey), eq("nonexistent"), anyString());
        }
    }

    @Nested
    @DisplayName("removeItem")
    class RemoveItem {

        @Test
        @DisplayName("Should remove item from Redis hash")
        void shouldRemoveItemFromRedisHash() {
            // given
            when(redisTemplate.opsForHash()).thenReturn(hashOperations);

            // when
            cartRedisRepository.removeItem(userId, "product-1");

            // then
            verify(hashOperations).delete(cartKey, "product-1");
        }

        @Test
        @DisplayName("Should use correct key pattern when removing")
        void shouldUseCorrectKeyPatternWhenRemoving() {
            // given
            when(redisTemplate.opsForHash()).thenReturn(hashOperations);
            String testUserId = "another-user";

            // when
            cartRedisRepository.removeItem(testUserId, "product-99");

            // then
            verify(hashOperations).delete("cart:another-user", "product-99");
        }
    }

    @Nested
    @DisplayName("clearCart")
    class ClearCart {

        @Test
        @DisplayName("Should delete entire cart key from Redis")
        void shouldDeleteEntireCartKeyFromRedis() {
            // when
            cartRedisRepository.clearCart(userId);

            // then
            verify(redisTemplate).delete(cartKey);
        }

        @Test
        @DisplayName("Should use correct key pattern when clearing")
        void shouldUseCorrectKeyPatternWhenClearing() {
            // given
            String testUserId = "clear-user";

            // when
            cartRedisRepository.clearCart(testUserId);

            // then
            verify(redisTemplate).delete("cart:clear-user");
        }
    }

    @Nested
    @DisplayName("existsItem")
    class ExistsItem {

        @Test
        @DisplayName("Should return true when item exists in Redis hash")
        void shouldReturnTrueWhenItemExists() {
            // given
            when(redisTemplate.opsForHash()).thenReturn(hashOperations);
            when(hashOperations.hasKey(cartKey, "product-1")).thenReturn(true);

            // when
            boolean result = cartRedisRepository.existsItem(userId, "product-1");

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return false when item does not exist in Redis hash")
        void shouldReturnFalseWhenItemDoesNotExist() {
            // given
            when(redisTemplate.opsForHash()).thenReturn(hashOperations);
            when(hashOperations.hasKey(cartKey, "nonexistent")).thenReturn(false);

            // when
            boolean result = cartRedisRepository.existsItem(userId, "nonexistent");

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("getItem")
    class GetItem {

        @Test
        @DisplayName("Should return cart item when found")
        void shouldReturnCartItemWhenFound() throws JsonProcessingException {
            // given
            String serializedItem = objectMapper.writeValueAsString(sampleItem);

            when(redisTemplate.opsForHash()).thenReturn(hashOperations);
            when(hashOperations.get(cartKey, "product-1")).thenReturn(serializedItem);

            // when
            Optional<CartItem> result = cartRedisRepository.getItem(userId, "product-1");

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getProductId()).isEqualTo("product-1");
            assertThat(result.get().getQuantity()).isEqualTo(2);
        }

        @Test
        @DisplayName("Should return empty optional when item not found")
        void shouldReturnEmptyOptionalWhenNotFound() {
            // given
            when(redisTemplate.opsForHash()).thenReturn(hashOperations);
            when(hashOperations.get(cartKey, "nonexistent")).thenReturn(null);

            // when
            Optional<CartItem> result = cartRedisRepository.getItem(userId, "nonexistent");

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty optional when deserialization fails")
        void shouldReturnEmptyOptionalWhenDeserializationFails() {
            // given
            when(redisTemplate.opsForHash()).thenReturn(hashOperations);
            when(hashOperations.get(cartKey, "bad-item")).thenReturn("not-valid-json");

            // when
            Optional<CartItem> result = cartRedisRepository.getItem(userId, "bad-item");

            // then
            assertThat(result).isEmpty();
        }
    }
}
