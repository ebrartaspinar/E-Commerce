package com.trendyolclone.order.domain.event;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCreatedEvent(
    UUID orderId,
    String orderNumber,
    UUID userId,
    BigDecimal totalAmount,
    int itemCount
) {}
