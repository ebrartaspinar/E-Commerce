package com.trendyolclone.payment.domain.service;

public record PaymentResult(
        boolean success,
        String transactionId,
        String failureReason
) {
}
