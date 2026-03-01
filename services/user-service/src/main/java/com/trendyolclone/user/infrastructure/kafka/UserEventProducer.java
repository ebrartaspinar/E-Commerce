package com.trendyolclone.user.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trendyolclone.common.event.DomainEvent;
import com.trendyolclone.common.kafka.KafkaTopics;
import com.trendyolclone.user.domain.event.UserRegisteredEvent;
import com.trendyolclone.user.domain.event.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventProducer {

    private static final Logger log = LoggerFactory.getLogger(UserEventProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishUserRegistered(UserRegisteredEvent event) {
        DomainEvent<UserRegisteredEvent> domainEvent = DomainEvent.of(
                "USER_REGISTERED",
                event.userId(),
                "User",
                event
        );

        String key = event.userId().toString();
        String value = serialize(domainEvent);

        kafkaTemplate.send(KafkaTopics.USER_EVENTS, key, value)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish USER_REGISTERED event for userId={}: {}",
                                event.userId(), ex.getMessage());
                    } else {
                        log.info("Published USER_REGISTERED event for userId={} to topic={}, partition={}, offset={}",
                                event.userId(),
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }

    public void publishUserUpdated(UserUpdatedEvent event) {
        DomainEvent<UserUpdatedEvent> domainEvent = DomainEvent.of(
                "USER_UPDATED",
                event.userId(),
                "User",
                event
        );

        String key = event.userId().toString();
        String value = serialize(domainEvent);

        kafkaTemplate.send(KafkaTopics.USER_EVENTS, key, value)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish USER_UPDATED event for userId={}: {}",
                                event.userId(), ex.getMessage());
                    } else {
                        log.info("Published USER_UPDATED event for userId={} to topic={}, partition={}, offset={}",
                                event.userId(),
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
