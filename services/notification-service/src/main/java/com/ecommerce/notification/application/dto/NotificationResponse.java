package com.ecommerce.notification.application.dto;

import com.ecommerce.notification.domain.model.NotificationChannel;
import com.ecommerce.notification.domain.model.NotificationStatus;
import com.ecommerce.notification.domain.model.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID userId,
        NotificationType type,
        NotificationChannel channel,
        String title,
        String content,
        NotificationStatus status,
        boolean isRead,
        LocalDateTime createdAt,
        LocalDateTime sentAt
) {
}
