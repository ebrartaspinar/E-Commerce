package com.stardrop.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void sendOrderEvent(OrderEvent event) {
        log.info(">>> Kafka: Sending order event for order #{}", event.getOrderId());
        kafkaTemplate.send("order-events", String.valueOf(event.getOrderId()), event);
    }
}
