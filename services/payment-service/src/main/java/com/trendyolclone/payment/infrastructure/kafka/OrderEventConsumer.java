package com.trendyolclone.payment.infrastructure.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trendyolclone.common.kafka.KafkaTopics;
import com.trendyolclone.payment.application.dto.InitiatePaymentRequest;
import com.trendyolclone.payment.application.service.PaymentService;
import com.trendyolclone.payment.domain.model.PaymentMethod;
import com.trendyolclone.payment.domain.model.PaymentStatus;
import com.trendyolclone.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopics.ORDER_EVENTS, groupId = "payment-service-group")
    public void listenOrderEvents(String message) {
        try {
            log.info("Received order event: {}", message);

            JsonNode root = objectMapper.readTree(message);
            String eventType = root.get("eventType").asText();

            switch (eventType) {
                case "order.created" -> handleOrderCreated(root);
                case "order.cancelled" -> handleOrderCancelled(root);
                default -> log.debug("Ignoring order event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Error processing order event: {}", e.getMessage(), e);
        }
    }

    private void handleOrderCreated(JsonNode root) {
        try {
            JsonNode payload = root.get("payload");
            String orderNumber = payload.get("orderNumber").asText();
            UUID userId = UUID.fromString(payload.get("userId").asText());
            BigDecimal totalAmount = new BigDecimal(payload.get("totalAmount").asText());

            String paymentMethodStr = payload.has("paymentMethod")
                    ? payload.get("paymentMethod").asText()
                    : "CREDIT_CARD";
            PaymentMethod method = PaymentMethod.valueOf(paymentMethodStr);

            log.info("Auto-initiating payment for orderNumber={}, userId={}, amount={}",
                    orderNumber, userId, totalAmount);

            InitiatePaymentRequest request = new InitiatePaymentRequest(
                    orderNumber, userId, totalAmount, method
            );

            paymentService.initiatePayment(request);
        } catch (Exception e) {
            log.error("Error handling order.created event: {}", e.getMessage(), e);
        }
    }

    private void handleOrderCancelled(JsonNode root) {
        try {
            JsonNode payload = root.get("payload");
            String orderNumber = payload.get("orderNumber").asText();

            log.info("Processing order cancellation for orderNumber={}", orderNumber);

            paymentRepository.findByOrderNumber(orderNumber).ifPresent(payment -> {
                if (payment.getStatus() == PaymentStatus.COMPLETED) {
                    log.info("Refunding payment for cancelled order orderNumber={}, paymentId={}",
                            orderNumber, payment.getId());
                    paymentService.refundPayment(payment.getId());
                } else {
                    log.info("Payment for orderNumber={} is not COMPLETED (status={}), skipping refund",
                            orderNumber, payment.getStatus());
                }
            });
        } catch (Exception e) {
            log.error("Error handling order.cancelled event: {}", e.getMessage(), e);
        }
    }
}
