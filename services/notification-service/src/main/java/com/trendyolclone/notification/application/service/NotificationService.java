package com.trendyolclone.notification.application.service;

import com.trendyolclone.common.dto.PagedResponse;
import com.trendyolclone.common.exception.ResourceNotFoundException;
import com.trendyolclone.notification.application.dto.NotificationResponse;
import com.trendyolclone.notification.domain.model.Notification;
import com.trendyolclone.notification.domain.model.NotificationChannel;
import com.trendyolclone.notification.domain.model.NotificationStatus;
import com.trendyolclone.notification.domain.model.NotificationType;
import com.trendyolclone.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    public PagedResponse<NotificationResponse> getNotifications(UUID userId, Pageable pageable) {
        Page<NotificationResponse> page = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toResponse);
        return PagedResponse.from(page);
    }

    @Transactional
    @CacheEvict(value = "unread-count", key = "#userId")
    public NotificationResponse markAsRead(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Notification", "id", notificationId);
        }

        notification.setRead(true);
        notification = notificationRepository.save(notification);

        log.info("Notification marked as read notificationId={}, userId={}", notificationId, userId);

        return toResponse(notification);
    }

    @Transactional
    @CacheEvict(value = "unread-count", key = "#userId")
    public void markAllAsRead(UUID userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalse(userId);

        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);

        log.info("Marked {} notifications as read for userId={}", unreadNotifications.size(), userId);
    }

    @Cacheable(value = "unread-count", key = "#userId")
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    @CacheEvict(value = "unread-count", key = "#userId")
    public NotificationResponse createNotification(UUID userId, NotificationChannel channel, String title, String content) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(NotificationType.IN_APP)
                .channel(channel)
                .title(title)
                .content(content)
                .status(NotificationStatus.SENT)
                .isRead(false)
                .sentAt(LocalDateTime.now())
                .build();

        notification = notificationRepository.save(notification);

        log.info("Created notification id={}, channel={}, userId={}", notification.getId(), channel, userId);

        return toResponse(notification);
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getUserId(),
                notification.getType(),
                notification.getChannel(),
                notification.getTitle(),
                notification.getContent(),
                notification.getStatus(),
                notification.isRead(),
                notification.getCreatedAt(),
                notification.getSentAt()
        );
    }
}
