package com.ecommerce.cart.domain.model;

import lombok.*;
import java.math.BigDecimal;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {
    private String userId;
    @Builder.Default
    private Map<String, CartItem> items = new HashMap<>();

    public BigDecimal getTotalAmount() {
        return items.values().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getItemCount() {
        return items.values().stream().mapToInt(CartItem::getQuantity).sum();
    }
}
