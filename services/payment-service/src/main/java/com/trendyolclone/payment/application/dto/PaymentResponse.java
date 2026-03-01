package com.trendyolclone.payment.application.dto;

import com.trendyolclone.payment.domain.model.PaymentMethod;
import com.trendyolclone.payment.domain.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        String orderNumber,
        UUID userId,
        BigDecimal amount,
        String currency,
        PaymentMethod method,
        PaymentStatus status,
        String transactionId,
        String failureReason,
        LocalDateTime createdAt,
        LocalDateTime completedAt
) {
}
