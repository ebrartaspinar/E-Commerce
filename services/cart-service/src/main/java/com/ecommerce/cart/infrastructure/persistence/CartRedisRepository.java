package com.ecommerce.cart.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecommerce.cart.domain.model.Cart;
import com.ecommerce.cart.domain.model.CartItem;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class CartRedisRepository {

    private static final Logger log = LoggerFactory.getLogger(CartRedisRepository.class);
    private static final String CART_KEY_PREFIX = "cart:";
    private static final long CART_TTL_DAYS = 30;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public Cart findByUserId(String userId) {
        String key = buildKey(userId);
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);

        Map<String, CartItem> items = new HashMap<>();
        entries.forEach((productId, json) -> {
            try {
                CartItem item = objectMapper.readValue((String) json, CartItem.class);
                items.put((String) productId, item);
            } catch (JsonProcessingException e) {
                log.error("Failed to deserialize cart item for userId={}, productId={}: {}",
                        userId, productId, e.getMessage());
            }
        });

        return Cart.builder()
                .userId(userId)
                .items(items)
                .build();
    }

    public void addItem(String userId, CartItem item) {
        String key = buildKey(userId);
        String json = serialize(item);
        redisTemplate.opsForHash().put(key, item.getProductId(), json);
        redisTemplate.expire(key, CART_TTL_DAYS, TimeUnit.DAYS);
    }

    public void updateItemQuantity(String userId, String productId, int quantity) {
        String key = buildKey(userId);
        String json = (String) redisTemplate.opsForHash().get(key, productId);
        if (json != null) {
            try {
                CartItem item = objectMapper.readValue(json, CartItem.class);
                item.setQuantity(quantity);
                redisTemplate.opsForHash().put(key, productId, serialize(item));
                redisTemplate.expire(key, CART_TTL_DAYS, TimeUnit.DAYS);
            } catch (JsonProcessingException e) {
                log.error("Failed to update quantity for userId={}, productId={}: {}",
                        userId, productId, e.getMessage());
            }
        }
    }

    public void removeItem(String userId, String productId) {
        String key = buildKey(userId);
        redisTemplate.opsForHash().delete(key, productId);
    }

    public void clearCart(String userId) {
        String key = buildKey(userId);
        redisTemplate.delete(key);
    }

    public boolean existsItem(String userId, String productId) {
        String key = buildKey(userId);
        return redisTemplate.opsForHash().hasKey(key, productId);
    }

    public Optional<CartItem> getItem(String userId, String productId) {
        String key = buildKey(userId);
        String json = (String) redisTemplate.opsForHash().get(key, productId);
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(json, CartItem.class));
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize cart item for userId={}, productId={}: {}",
                    userId, productId, e.getMessage());
            return Optional.empty();
        }
    }

    private String buildKey(String userId) {
        return CART_KEY_PREFIX + userId;
    }

    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object: {}", e.getMessage());
            throw new RuntimeException("Failed to serialize cart item", e);
        }
    }
}
