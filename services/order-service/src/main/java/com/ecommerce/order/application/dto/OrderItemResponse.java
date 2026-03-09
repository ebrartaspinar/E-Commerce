package com.ecommerce.order.application.dto;

import com.ecommerce.order.domain.model.OrderItemStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
    UUID id,
    UUID productId,
    UUID sellerId,
    String productName,
    String productImage,
    BigDecimal unitPrice,
    int quantity,
    BigDecimal totalPrice,
    OrderItemStatus status
) {}
