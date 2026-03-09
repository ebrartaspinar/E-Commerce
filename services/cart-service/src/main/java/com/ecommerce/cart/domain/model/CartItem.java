package com.ecommerce.cart.domain.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    private String productId;
    private String productName;
    private BigDecimal price;
    private int quantity;
    private String imageUrl;
    private String sellerId;
    private LocalDateTime addedAt;
    private boolean priceChanged;
    private BigDecimal currentPrice;
    private boolean available;
}
