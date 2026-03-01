package com.trendyolclone.common.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record DomainEvent<T>(
    UUID eventId,
    String eventType,
    LocalDateTime timestamp,
    UUID aggregateId,
    String aggregateType,
    T payload
) {
    public static <T> DomainEvent<T> of(String eventType, UUID aggregateId, String aggregateType, T payload) {
        return new DomainEvent<>(UUID.randomUUID(), eventType, LocalDateTime.now(), aggregateId, aggregateType, payload);
    }
}
