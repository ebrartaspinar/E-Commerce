package com.ecommerce.cart.application.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
    String userId,
    List<CartItemResponse> items,
    BigDecimal totalAmount,
    int itemCount
) {}
