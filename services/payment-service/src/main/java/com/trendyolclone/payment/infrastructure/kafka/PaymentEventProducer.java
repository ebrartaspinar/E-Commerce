package com.trendyolclone.payment.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trendyolclone.common.event.DomainEvent;
import com.trendyolclone.common.kafka.KafkaTopics;
import com.trendyolclone.payment.domain.event.PaymentCompletedEvent;
import com.trendyolclone.payment.domain.event.PaymentFailedEvent;
import com.trendyolclone.payment.domain.model.Payment;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishPaymentCompleted(PaymentCompletedEvent event) {
        DomainEvent<PaymentCompletedEvent> domainEvent = DomainEvent.of(
                "payment.completed",
                event.paymentId(),
                "Payment",
                event
        );

        String key = event.paymentId().toString();
        String value = serialize(domainEvent);

        kafkaTemplate.send(KafkaTopics.PAYMENT_EVENTS, key, value)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish payment.completed event for paymentId={}: {}",
                                event.paymentId(), ex.getMessage());
                    } else {
                        log.info("Published payment.completed event for paymentId={} to topic={}, partition={}, offset={}",
                                event.paymentId(),
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }

    public void publishPaymentFailed(PaymentFailedEvent event) {
        DomainEvent<PaymentFailedEvent> domainEvent = DomainEvent.of(
                "payment.failed",
                event.paymentId(),
                "Payment",
                event
        );

        String key = event.paymentId().toString();
        String value = serialize(domainEvent);

        kafkaTemplate.send(KafkaTopics.PAYMENT_EVENTS, key, value)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish payment.failed event for paymentId={}: {}",
                                event.paymentId(), ex.getMessage());
                    } else {
                        log.info("Published payment.failed event for paymentId={} to topic={}, partition={}, offset={}",
                                event.paymentId(),
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }

    public void publishPaymentRefunded(Payment payment) {
        DomainEvent<Object> domainEvent = DomainEvent.of(
                "payment.refunded",
                payment.getId(),
                "Payment",
                new PaymentRefundedPayload(
                        payment.getId(),
                        payment.getOrderNumber(),
                        payment.getUserId(),
                        payment.getAmount(),
                        payment.getTransactionId()
                )
        );

        String key = payment.getId().toString();
        String value = serialize(domainEvent);

        kafkaTemplate.send(KafkaTopics.PAYMENT_EVENTS, key, value)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish payment.refunded event for paymentId={}: {}",
                                payment.getId(), ex.getMessage());
                    } else {
                        log.info("Published payment.refunded event for paymentId={} to topic={}, partition={}, offset={}",
                                payment.getId(),
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

    private record PaymentRefundedPayload(
            java.util.UUID paymentId,
            String orderNumber,
            java.util.UUID userId,
            java.math.BigDecimal amount,
            String transactionId
    ) {
    }
}
