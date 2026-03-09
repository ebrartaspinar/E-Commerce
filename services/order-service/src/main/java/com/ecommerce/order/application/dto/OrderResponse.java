package com.ecommerce.order.application.dto;

import com.ecommerce.order.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
    UUID id,
    String orderNumber,
    UUID userId,
    OrderStatus status,
    List<OrderItemResponse> items,
    OrderAddressResponse shippingAddress,
    BigDecimal totalAmount,
    BigDecimal discountAmount,
    BigDecimal shippingCost,
    BigDecimal finalAmount,
    LocalDateTime createdAt
) {}
