package com.stardrop.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationListener {

    @KafkaListener(topics = "order-events", groupId = "stardrop-group")
    public void handleOrderEvent(OrderEvent event) {
        log.info(">>> Notification: Order #{} - Status: {} - Total: {} TL - Items: {}",
                event.getOrderId(),
                event.getStatus(),
                event.getTotalAmount(),
                event.getItemCount());

        // Burada gerçek projede email/SMS/push notification gönderilir
        // Biz sadece log'luyoruz (3. sınıf seviyesi)
        switch (event.getStatus()) {
            case "CREATED" -> log.info(">>> Mail gonderildi: Siparissiniz alindi!");
            case "CANCELLED" -> log.info(">>> Mail gonderildi: Siparissiniz iptal edildi.");
        }
    }
}
