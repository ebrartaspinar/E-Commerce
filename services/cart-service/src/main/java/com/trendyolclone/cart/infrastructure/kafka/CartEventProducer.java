package com.trendyolclone.cart.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trendyolclone.common.event.DomainEvent;
import com.trendyolclone.common.kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CartEventProducer {

    private static final Logger log = LoggerFactory.getLogger(CartEventProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishCheckoutInitiated(String userId, Map<String, Object> checkoutData) {
        DomainEvent<Map<String, Object>> domainEvent = DomainEvent.of(
                "CART_CHECKOUT_INITIATED",
                UUID.fromString(userId),
                "Cart",
                checkoutData
        );

        String key = userId;
        String value = serialize(domainEvent);

        kafkaTemplate.send(KafkaTopics.CART_EVENTS, key, value)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish CART_CHECKOUT_INITIATED event for userId={}: {}",
                                userId, ex.getMessage());
                    } else {
                        log.info("Published CART_CHECKOUT_INITIATED event for userId={} to topic={}, partition={}, offset={}",
                                userId,
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
