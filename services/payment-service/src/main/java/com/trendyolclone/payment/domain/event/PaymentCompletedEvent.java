package com.trendyolclone.payment.domain.event;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentCompletedEvent(
        UUID paymentId,
        String orderNumber,
        UUID userId,
        BigDecimal amount,
        String transactionId
) {
}
