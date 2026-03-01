package com.trendyolclone.payment.domain.event;

import java.util.UUID;

public record PaymentFailedEvent(
        UUID paymentId,
        String orderNumber,
        UUID userId,
        String reason
) {
}
