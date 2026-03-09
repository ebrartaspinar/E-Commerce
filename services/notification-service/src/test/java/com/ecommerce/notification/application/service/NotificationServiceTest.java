package com.ecommerce.notification.application.service;

import com.ecommerce.common.dto.PagedResponse;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.notification.application.dto.NotificationResponse;
import com.ecommerce.notification.domain.model.Notification;
import com.ecommerce.notification.domain.model.NotificationChannel;
import com.ecommerce.notification.domain.model.NotificationStatus;
import com.ecommerce.notification.domain.model.NotificationType;
import com.ecommerce.notification.domain.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Unit Tests")
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private UUID userId;
    private UUID notificationId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        notificationId = UUID.randomUUID();
    }

    private Notification createTestNotification(boolean isRead) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(NotificationType.IN_APP)
                .channel(NotificationChannel.ORDER_CONFIRMATION)
                .title("Order Confirmed")
                .content("Your order TY-20260301-ABC123 has been placed successfully!")
                .status(NotificationStatus.SENT)
                .isRead(isRead)
                .sentAt(LocalDateTime.now())
                .build();
        notification.setId(notificationId);
        notification.setCreatedAt(LocalDateTime.now());
        return notification;
    }

    @Nested
    @DisplayName("getNotifications")
    class GetNotificationsTests {

        @Test
        @DisplayName("Should return paged notifications for user")
        void shouldReturnPagedNotificationsForUser() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            Notification notification = createTestNotification(false);
            Page<Notification> notificationPage = new PageImpl<>(List.of(notification), pageable, 1);
            when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable))
                    .thenReturn(notificationPage);

            // when
            PagedResponse<NotificationResponse> response = notificationService.getNotifications(userId, pageable);

            // then
            assertThat(response).isNotNull();
            assertThat(response.content()).hasSize(1);
            assertThat(response.content().get(0).title()).isEqualTo("Order Confirmed");
            assertThat(response.content().get(0).userId()).isEqualTo(userId);
            assertThat(response.totalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should return empty paged response when no notifications exist")
        void shouldReturnEmptyPagedResponse() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            Page<Notification> emptyPage = new PageImpl<>(List.of(), pageable, 0);
            when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable))
                    .thenReturn(emptyPage);

            // when
            PagedResponse<NotificationResponse> response = notificationService.getNotifications(userId, pageable);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should return multiple notifications in paged response")
        void shouldReturnMultipleNotifications() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            Notification notification1 = createTestNotification(false);
            Notification notification2 = Notification.builder()
                    .userId(userId)
                    .type(NotificationType.IN_APP)
                    .channel(NotificationChannel.PAYMENT_SUCCESS)
                    .title("Payment Successful")
                    .content("Payment of 100 TRY was successful.")
                    .status(NotificationStatus.SENT)
                    .isRead(true)
                    .sentAt(LocalDateTime.now())
                    .build();
            notification2.setId(UUID.randomUUID());
            notification2.setCreatedAt(LocalDateTime.now());

            Page<Notification> notificationPage = new PageImpl<>(
                    List.of(notification1, notification2), pageable, 2);
            when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable))
                    .thenReturn(notificationPage);

            // when
            PagedResponse<NotificationResponse> response = notificationService.getNotifications(userId, pageable);

            // then
            assertThat(response.content()).hasSize(2);
            assertThat(response.totalElements()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("markAsRead")
    class MarkAsReadTests {

        @Test
        @DisplayName("Should mark notification as read successfully")
        void shouldMarkNotificationAsReadSuccessfully() {
            // given
            Notification notification = createTestNotification(false);
            when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
            when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            NotificationResponse response = notificationService.markAsRead(userId, notificationId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.isRead()).isTrue();
            verify(notificationRepository).save(notification);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when notification not found")
        void shouldThrowWhenNotificationNotFound() {
            // given
            when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> notificationService.markAsRead(userId, notificationId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Notification");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user does not own the notification")
        void shouldThrowWhenWrongUser() {
            // given
            Notification notification = createTestNotification(false);
            UUID differentUserId = UUID.randomUUID();
            when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

            // when/then
            assertThatThrownBy(() -> notificationService.markAsRead(differentUserId, notificationId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Notification");
        }

        @Test
        @DisplayName("Should handle marking already read notification")
        void shouldHandleAlreadyReadNotification() {
            // given
            Notification notification = createTestNotification(true);
            when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
            when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            NotificationResponse response = notificationService.markAsRead(userId, notificationId);

            // then
            assertThat(response.isRead()).isTrue();
        }
    }

    @Nested
    @DisplayName("markAllAsRead")
    class MarkAllAsReadTests {

        @Test
        @DisplayName("Should mark all unread notifications as read")
        void shouldMarkAllUnreadAsRead() {
            // given
            Notification notification1 = createTestNotification(false);
            Notification notification2 = Notification.builder()
                    .userId(userId)
                    .type(NotificationType.IN_APP)
                    .channel(NotificationChannel.PAYMENT_SUCCESS)
                    .title("Payment Successful")
                    .content("Payment was successful.")
                    .status(NotificationStatus.SENT)
                    .isRead(false)
                    .sentAt(LocalDateTime.now())
                    .build();
            notification2.setId(UUID.randomUUID());

            when(notificationRepository.findByUserIdAndIsReadFalse(userId))
                    .thenReturn(List.of(notification1, notification2));

            // when
            notificationService.markAllAsRead(userId);

            // then
            assertThat(notification1.isRead()).isTrue();
            assertThat(notification2.isRead()).isTrue();
            verify(notificationRepository).saveAll(List.of(notification1, notification2));
        }

        @Test
        @DisplayName("Should handle no unread notifications gracefully")
        void shouldHandleNoUnreadNotifications() {
            // given
            when(notificationRepository.findByUserIdAndIsReadFalse(userId)).thenReturn(List.of());

            // when
            notificationService.markAllAsRead(userId);

            // then
            verify(notificationRepository).saveAll(List.of());
        }
    }

    @Nested
    @DisplayName("getUnreadCount")
    class GetUnreadCountTests {

        @Test
        @DisplayName("Should return correct unread count")
        void shouldReturnCorrectUnreadCount() {
            // given
            when(notificationRepository.countByUserIdAndIsReadFalse(userId)).thenReturn(5L);

            // when
            long count = notificationService.getUnreadCount(userId);

            // then
            assertThat(count).isEqualTo(5L);
        }

        @Test
        @DisplayName("Should return zero when no unread notifications")
        void shouldReturnZeroWhenNoUnread() {
            // given
            when(notificationRepository.countByUserIdAndIsReadFalse(userId)).thenReturn(0L);

            // when
            long count = notificationService.getUnreadCount(userId);

            // then
            assertThat(count).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("createNotification")
    class CreateNotificationTests {

        @Test
        @DisplayName("Should create and save notification successfully")
        void shouldCreateAndSaveNotification() {
            // given
            when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
                Notification saved = invocation.getArgument(0);
                saved.setId(notificationId);
                saved.setCreatedAt(LocalDateTime.now());
                return saved;
            });

            // when
            NotificationResponse response = notificationService.createNotification(
                    userId,
                    NotificationChannel.ORDER_CONFIRMATION,
                    "Order Confirmed",
                    "Your order has been placed successfully!"
            );

            // then
            assertThat(response).isNotNull();
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.channel()).isEqualTo(NotificationChannel.ORDER_CONFIRMATION);
            assertThat(response.title()).isEqualTo("Order Confirmed");
            assertThat(response.content()).isEqualTo("Your order has been placed successfully!");
            assertThat(response.type()).isEqualTo(NotificationType.IN_APP);
            assertThat(response.status()).isEqualTo(NotificationStatus.SENT);
            assertThat(response.isRead()).isFalse();
            verify(notificationRepository).save(any(Notification.class));
        }

        @Test
        @DisplayName("Should create notification with WELCOME channel")
        void shouldCreateWelcomeNotification() {
            // given
            when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
                Notification saved = invocation.getArgument(0);
                saved.setId(UUID.randomUUID());
                saved.setCreatedAt(LocalDateTime.now());
                return saved;
            });

            // when
            NotificationResponse response = notificationService.createNotification(
                    userId,
                    NotificationChannel.WELCOME,
                    "Welcome to E-Commerce!",
                    "Welcome to E-Commerce, Test User!"
            );

            // then
            assertThat(response.channel()).isEqualTo(NotificationChannel.WELCOME);
            assertThat(response.title()).isEqualTo("Welcome to E-Commerce!");
        }

        @Test
        @DisplayName("Should set sentAt timestamp when creating notification")
        void shouldSetSentAtTimestamp() {
            // given
            when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
                Notification saved = invocation.getArgument(0);
                saved.setId(UUID.randomUUID());
                saved.setCreatedAt(LocalDateTime.now());
                return saved;
            });

            // when
            NotificationResponse response = notificationService.createNotification(
                    userId,
                    NotificationChannel.PAYMENT_SUCCESS,
                    "Payment Successful",
                    "Payment completed"
            );

            // then
            assertThat(response.sentAt()).isNotNull();
        }
    }
}
