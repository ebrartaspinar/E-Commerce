package com.trendyolclone.order.domain.event;

import java.util.UUID;

public record OrderCancelledEvent(
    UUID orderId,
    String orderNumber,
    UUID userId,
    String reason
) {}
