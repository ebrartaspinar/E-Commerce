package com.ecommerce.payment.application.dto;

import com.ecommerce.payment.domain.model.PaymentMethod;
import com.ecommerce.payment.domain.model.PaymentStatus;

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
