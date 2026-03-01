package com.trendyolclone.product.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trendyolclone.common.event.DomainEvent;
import com.trendyolclone.common.kafka.KafkaTopics;
import com.trendyolclone.product.domain.event.ProductCreatedEvent;
import com.trendyolclone.product.domain.event.ProductStockUpdatedEvent;
import com.trendyolclone.product.domain.event.ProductUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEventProducer {

    private static final Logger log = LoggerFactory.getLogger(ProductEventProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishProductCreated(ProductCreatedEvent event) {
        DomainEvent<ProductCreatedEvent> domainEvent = DomainEvent.of(
                "PRODUCT_CREATED",
                event.productId(),
                "Product",
                event
        );
        sendEvent(KafkaTopics.PRODUCT_EVENTS, event.productId().toString(), domainEvent);
        log.info("Published ProductCreatedEvent for product: {}", event.productId());
    }

    public void publishProductUpdated(ProductUpdatedEvent event) {
        DomainEvent<ProductUpdatedEvent> domainEvent = DomainEvent.of(
                "PRODUCT_UPDATED",
                event.productId(),
                "Product",
                event
        );
        sendEvent(KafkaTopics.PRODUCT_EVENTS, event.productId().toString(), domainEvent);
        log.info("Published ProductUpdatedEvent for product: {}", event.productId());
    }

    public void publishProductStockUpdated(ProductStockUpdatedEvent event) {
        DomainEvent<ProductStockUpdatedEvent> domainEvent = DomainEvent.of(
                "PRODUCT_STOCK_UPDATED",
                event.productId(),
                "Product",
                event
        );
        sendEvent(KafkaTopics.PRODUCT_STOCK_EVENTS, event.productId().toString(), domainEvent);
        log.info("Published ProductStockUpdatedEvent for product: {}, stock: {}", event.productId(), event.stockQuantity());
    }

    private void sendEvent(String topic, String key, Object event) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, key, jsonPayload)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to send event to topic {}: {}", topic, ex.getMessage());
                        } else {
                            log.debug("Event sent to topic {} with offset {}", topic,
                                    result.getRecordMetadata().offset());
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event for topic {}: {}", topic, e.getMessage());
        }
    }
}
