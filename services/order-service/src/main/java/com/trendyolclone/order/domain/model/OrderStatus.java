package com.trendyolclone.order.domain.model;

import java.util.Map;
import java.util.Set;

public enum OrderStatus {
    CREATED,
    PAYMENT_PENDING,
    PAYMENT_COMPLETED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUND_REQUESTED,
    REFUNDED;

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
        CREATED, Set.of(PAYMENT_PENDING, CANCELLED),
        PAYMENT_PENDING, Set.of(PAYMENT_COMPLETED, CANCELLED),
        PAYMENT_COMPLETED, Set.of(PROCESSING, CANCELLED),
        PROCESSING, Set.of(SHIPPED, CANCELLED),
        SHIPPED, Set.of(DELIVERED),
        DELIVERED, Set.of(REFUND_REQUESTED),
        CANCELLED, Set.of(),
        REFUND_REQUESTED, Set.of(REFUNDED),
        REFUNDED, Set.of()
    );

    public boolean canTransitionTo(OrderStatus target) {
        return ALLOWED_TRANSITIONS.getOrDefault(this, Set.of()).contains(target);
    }
}
