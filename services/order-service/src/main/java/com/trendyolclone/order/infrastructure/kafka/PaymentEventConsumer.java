package com.trendyolclone.order.infrastructure.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trendyolclone.common.kafka.KafkaTopics;
import com.trendyolclone.order.application.dto.CancelOrderRequest;
import com.trendyolclone.order.application.service.OrderService;
import com.trendyolclone.order.domain.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopics.PAYMENT_EVENTS, groupId = "order-service-group")
    public void handlePaymentEvent(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);
            String eventType = event.get("eventType").asText();
            JsonNode payload = event.get("payload");

            switch (eventType) {
                case "PAYMENT_COMPLETED" -> handlePaymentCompleted(payload);
                case "PAYMENT_FAILED" -> handlePaymentFailed(payload);
                default -> log.warn("Received unknown payment event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Failed to process payment event: {}", e.getMessage(), e);
        }
    }

    private void handlePaymentCompleted(JsonNode payload) {
        try {
            String orderNumber = payload.get("orderNumber").asText();
            UUID paymentId = UUID.fromString(payload.get("paymentId").asText());
            log.info("Processing PAYMENT_COMPLETED event for orderNumber={}, paymentId={}", orderNumber, paymentId);
            orderService.updatePaymentStatus(orderNumber, OrderStatus.PAYMENT_COMPLETED, paymentId);
        } catch (Exception e) {
            log.error("Failed to handle payment completed event: {}", e.getMessage(), e);
        }
    }

    private void handlePaymentFailed(JsonNode payload) {
        try {
            String orderNumber = payload.get("orderNumber").asText();
            String reason = payload.has("reason") ? payload.get("reason").asText() : "Payment failed";
            log.info("Processing PAYMENT_FAILED event for orderNumber={}, reason={}", orderNumber, reason);
            // For payment failures, we cancel the order
            // Note: userId is extracted from the order itself in cancelOrder
            // For now, just log since we need the userId to cancel
            log.warn("Payment failed for orderNumber={}. Manual cancellation may be required. Reason: {}",
                    orderNumber, reason);
        } catch (Exception e) {
            log.error("Failed to handle payment failed event: {}", e.getMessage(), e);
        }
    }
}
