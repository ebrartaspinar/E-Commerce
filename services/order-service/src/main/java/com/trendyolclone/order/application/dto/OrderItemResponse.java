package com.trendyolclone.order.application.dto;

import com.trendyolclone.order.domain.model.OrderItemStatus;

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
