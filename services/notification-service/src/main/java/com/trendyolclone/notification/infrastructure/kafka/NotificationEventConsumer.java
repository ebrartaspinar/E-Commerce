package com.trendyolclone.notification.infrastructure.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trendyolclone.common.kafka.KafkaTopics;
import com.trendyolclone.notification.application.service.NotificationService;
import com.trendyolclone.notification.application.service.NotificationTemplateService;
import com.trendyolclone.notification.domain.model.NotificationChannel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventConsumer.class);

    private final NotificationService notificationService;
    private final NotificationTemplateService templateService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopics.USER_EVENTS, groupId = "notification-service-group")
    public void listenUserEvents(String message) {
        try {
            log.info("Received user event: {}", message);

            JsonNode root = objectMapper.readTree(message);
            String eventType = root.get("eventType").asText();

            if ("USER_REGISTERED".equals(eventType) || "user.registered".equals(eventType)) {
                handleUserRegistered(root);
            } else {
                log.debug("Ignoring user event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Error processing user event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopics.ORDER_EVENTS, groupId = "notification-service-group")
    public void listenOrderEvents(String message) {
        try {
            log.info("Received order event: {}", message);

            JsonNode root = objectMapper.readTree(message);
            String eventType = root.get("eventType").asText();

            switch (eventType) {
                case "order.created" -> handleOrderCreated(root);
                case "order.shipped" -> handleOrderShipped(root);
                case "order.delivered" -> handleOrderDelivered(root);
                default -> log.debug("Ignoring order event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Error processing order event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_EVENTS, groupId = "notification-service-group")
    public void listenPaymentEvents(String message) {
        try {
            log.info("Received payment event: {}", message);

            JsonNode root = objectMapper.readTree(message);
            String eventType = root.get("eventType").asText();

            switch (eventType) {
                case "payment.completed" -> handlePaymentCompleted(root);
                case "payment.failed" -> handlePaymentFailed(root);
                default -> log.debug("Ignoring payment event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Error processing payment event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopics.PRODUCT_STOCK_EVENTS, groupId = "notification-service-group")
    public void listenProductStockEvents(String message) {
        try {
            log.info("Received product stock event: {}", message);

            JsonNode root = objectMapper.readTree(message);
            String eventType = root.get("eventType").asText();

            if ("product.stock.low".equals(eventType)) {
                handleStockLow(root);
            } else {
                log.debug("Ignoring product stock event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Error processing product stock event: {}", e.getMessage(), e);
        }
    }

    private void handleUserRegistered(JsonNode root) {
        JsonNode payload = root.get("payload");
        UUID userId = UUID.fromString(payload.get("userId").asText());
        String firstName = payload.has("firstName") ? payload.get("firstName").asText() : "User";

        NotificationChannel channel = NotificationChannel.WELCOME;
        String title = templateService.getTitleForChannel(channel);
        String content = templateService.renderTemplate(channel, Map.of("name", firstName));

        notificationService.createNotification(userId, channel, title, content);
        log.info("Created WELCOME notification for userId={}", userId);
    }

    private void handleOrderCreated(JsonNode root) {
        JsonNode payload = root.get("payload");
        UUID userId = UUID.fromString(payload.get("userId").asText());
        String orderNumber = payload.get("orderNumber").asText();

        NotificationChannel channel = NotificationChannel.ORDER_CONFIRMATION;
        String title = templateService.getTitleForChannel(channel);
        String content = templateService.renderTemplate(channel, Map.of("orderNumber", orderNumber));

        notificationService.createNotification(userId, channel, title, content);
        log.info("Created ORDER_CONFIRMATION notification for userId={}, orderNumber={}", userId, orderNumber);
    }

    private void handleOrderShipped(JsonNode root) {
        JsonNode payload = root.get("payload");
        UUID userId = UUID.fromString(payload.get("userId").asText());
        String orderNumber = payload.get("orderNumber").asText();

        NotificationChannel channel = NotificationChannel.ORDER_SHIPPED;
        String title = templateService.getTitleForChannel(channel);
        String content = templateService.renderTemplate(channel, Map.of("orderNumber", orderNumber));

        notificationService.createNotification(userId, channel, title, content);
        log.info("Created ORDER_SHIPPED notification for userId={}, orderNumber={}", userId, orderNumber);
    }

    private void handleOrderDelivered(JsonNode root) {
        JsonNode payload = root.get("payload");
        UUID userId = UUID.fromString(payload.get("userId").asText());
        String orderNumber = payload.get("orderNumber").asText();

        NotificationChannel channel = NotificationChannel.ORDER_DELIVERED;
        String title = templateService.getTitleForChannel(channel);
        String content = templateService.renderTemplate(channel, Map.of("orderNumber", orderNumber));

        notificationService.createNotification(userId, channel, title, content);
        log.info("Created ORDER_DELIVERED notification for userId={}, orderNumber={}", userId, orderNumber);
    }

    private void handlePaymentCompleted(JsonNode root) {
        JsonNode payload = root.get("payload");
        UUID userId = UUID.fromString(payload.get("userId").asText());
        String orderNumber = payload.get("orderNumber").asText();
        String amount = payload.has("amount") ? payload.get("amount").asText() : "0";

        NotificationChannel channel = NotificationChannel.PAYMENT_SUCCESS;
        String title = templateService.getTitleForChannel(channel);
        String content = templateService.renderTemplate(channel, Map.of(
                "amount", amount,
                "orderNumber", orderNumber
        ));

        notificationService.createNotification(userId, channel, title, content);
        log.info("Created PAYMENT_SUCCESS notification for userId={}, orderNumber={}", userId, orderNumber);
    }

    private void handlePaymentFailed(JsonNode root) {
        JsonNode payload = root.get("payload");
        UUID userId = UUID.fromString(payload.get("userId").asText());
        String orderNumber = payload.get("orderNumber").asText();

        NotificationChannel channel = NotificationChannel.PAYMENT_FAILED;
        String title = templateService.getTitleForChannel(channel);
        String content = templateService.renderTemplate(channel, Map.of("orderNumber", orderNumber));

        notificationService.createNotification(userId, channel, title, content);
        log.info("Created PAYMENT_FAILED notification for userId={}, orderNumber={}", userId, orderNumber);
    }

    private void handleStockLow(JsonNode root) {
        JsonNode payload = root.get("payload");
        // For stock alerts, the aggregateId from the DomainEvent can represent the seller/admin userId
        UUID userId = UUID.fromString(root.get("aggregateId").asText());
        String productName = payload.has("productName") ? payload.get("productName").asText() : "Unknown";
        String stockCount = payload.has("stockCount") ? payload.get("stockCount").asText() : "0";

        NotificationChannel channel = NotificationChannel.STOCK_LOW;
        String title = templateService.getTitleForChannel(channel);
        String content = templateService.renderTemplate(channel, Map.of(
                "productName", productName,
                "stockCount", stockCount
        ));

        notificationService.createNotification(userId, channel, title, content);
        log.info("Created STOCK_LOW notification for userId={}, productName={}", userId, productName);
    }
}
