package com.ecommerce.payment.domain.service;

public record PaymentResult(
        boolean success,
        String transactionId,
        String failureReason
) {
}
