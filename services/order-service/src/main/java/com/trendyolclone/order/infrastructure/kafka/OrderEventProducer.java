package com.trendyolclone.order.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trendyolclone.common.event.DomainEvent;
import com.trendyolclone.common.kafka.KafkaTopics;
import com.trendyolclone.order.domain.event.OrderCancelledEvent;
import com.trendyolclone.order.domain.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishOrderCreated(OrderCreatedEvent event) {
        DomainEvent<OrderCreatedEvent> domainEvent = DomainEvent.of(
                "ORDER_CREATED",
                event.orderId(),
                "Order",
                event
        );

        String key = event.orderId().toString();
        String value = serialize(domainEvent);

        kafkaTemplate.send(KafkaTopics.ORDER_EVENTS, key, value)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish ORDER_CREATED event for orderId={}: {}",
                                event.orderId(), ex.getMessage());
                    } else {
                        log.info("Published ORDER_CREATED event for orderId={} to topic={}, partition={}, offset={}",
                                event.orderId(),
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }

    public void publishOrderCancelled(OrderCancelledEvent event) {
        DomainEvent<OrderCancelledEvent> domainEvent = DomainEvent.of(
                "ORDER_CANCELLED",
                event.orderId(),
                "Order",
                event
        );

        String key = event.orderId().toString();
        String value = serialize(domainEvent);

        kafkaTemplate.send(KafkaTopics.ORDER_EVENTS, key, value)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish ORDER_CANCELLED event for orderId={}: {}",
                                event.orderId(), ex.getMessage());
                    } else {
                        log.info("Published ORDER_CANCELLED event for orderId={} to topic={}, partition={}, offset={}",
                                event.orderId(),
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }

    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event: {}", e.getMessage());
            throw new RuntimeException("Failed to serialize event", e);
        }
    }
}
