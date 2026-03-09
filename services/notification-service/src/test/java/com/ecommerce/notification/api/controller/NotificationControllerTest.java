package com.ecommerce.notification.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecommerce.common.dto.PagedResponse;
import com.ecommerce.notification.application.dto.NotificationResponse;
import com.ecommerce.notification.application.service.NotificationService;
import com.ecommerce.notification.domain.model.NotificationChannel;
import com.ecommerce.notification.domain.model.NotificationStatus;
import com.ecommerce.notification.domain.model.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import com.ecommerce.notification.infrastructure.config.SecurityConfig;
import com.ecommerce.notification.infrastructure.config.JwtAuthenticationFilter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = NotificationController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("NotificationController Integration Tests")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private UUID userId;
    private UUID notificationId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        notificationId = UUID.randomUUID();

        // Set up security context
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("GET /api/v1/notifications should return notifications and 200")
    void getNotificationsShouldReturn200() throws Exception {
        // given
        NotificationResponse notificationResponse = new NotificationResponse(
                notificationId,
                userId,
                NotificationType.IN_APP,
                NotificationChannel.ORDER_CONFIRMATION,
                "Order Confirmed",
                "Your order TY-20260301-ABC123 has been placed!",
                NotificationStatus.SENT,
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        PagedResponse<NotificationResponse> pagedResponse = new PagedResponse<>(
                List.of(notificationResponse), 0, 20, 1, 1, true, true
        );
        when(notificationService.getNotifications(eq(userId), any(Pageable.class))).thenReturn(pagedResponse);

        // when/then
        mockMvc.perform(get("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].title").value("Order Confirmed"))
                .andExpect(jsonPath("$.data.content[0].channel").value("ORDER_CONFIRMATION"))
                .andExpect(jsonPath("$.data.content[0].isRead").value(false))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/notifications should return empty list when no notifications")
    void getNotificationsShouldReturnEmptyList() throws Exception {
        // given
        PagedResponse<NotificationResponse> emptyResponse = new PagedResponse<>(
                List.of(), 0, 20, 0, 0, true, true
        );
        when(notificationService.getNotifications(eq(userId), any(Pageable.class))).thenReturn(emptyResponse);

        // when/then
        mockMvc.perform(get("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isEmpty())
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    @DisplayName("PATCH /api/v1/notifications/{id}/read should mark notification as read and return 200")
    void markAsReadShouldReturn200() throws Exception {
        // given
        NotificationResponse readResponse = new NotificationResponse(
                notificationId,
                userId,
                NotificationType.IN_APP,
                NotificationChannel.ORDER_CONFIRMATION,
                "Order Confirmed",
                "Your order has been placed!",
                NotificationStatus.SENT,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(notificationService.markAsRead(userId, notificationId)).thenReturn(readResponse);

        // when/then
        mockMvc.perform(patch("/api/v1/notifications/{id}/read", notificationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(notificationId.toString()))
                .andExpect(jsonPath("$.data.isRead").value(true))
                .andExpect(jsonPath("$.message").value("Notification marked as read"));
    }

    @Test
    @DisplayName("PATCH /api/v1/notifications/read-all should mark all as read and return 200")
    void markAllAsReadShouldReturn200() throws Exception {
        // when/then
        mockMvc.perform(patch("/api/v1/notifications/read-all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("All notifications marked as read"));

        verify(notificationService).markAllAsRead(userId);
    }

    @Test
    @DisplayName("GET /api/v1/notifications/unread-count should return unread count and 200")
    void getUnreadCountShouldReturn200() throws Exception {
        // given
        when(notificationService.getUnreadCount(userId)).thenReturn(7L);

        // when/then
        mockMvc.perform(get("/api/v1/notifications/unread-count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(7));
    }

    @Test
    @DisplayName("GET /api/v1/notifications/unread-count should return zero when no unread")
    void getUnreadCountShouldReturnZero() throws Exception {
        // given
        when(notificationService.getUnreadCount(userId)).thenReturn(0L);

        // when/then
        mockMvc.perform(get("/api/v1/notifications/unread-count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(0));
    }
}
