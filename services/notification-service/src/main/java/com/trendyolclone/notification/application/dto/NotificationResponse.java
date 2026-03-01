package com.trendyolclone.notification.application.dto;

import com.trendyolclone.notification.domain.model.NotificationChannel;
import com.trendyolclone.notification.domain.model.NotificationStatus;
import com.trendyolclone.notification.domain.model.NotificationType;

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
