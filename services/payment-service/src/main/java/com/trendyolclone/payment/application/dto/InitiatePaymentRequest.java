package com.trendyolclone.payment.application.dto;

import com.trendyolclone.payment.domain.model.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record InitiatePaymentRequest(
        @NotBlank(message = "Order number is required")
        String orderNumber,

        @NotNull(message = "User ID is required")
        UUID userId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
        BigDecimal amount,

        @NotNull(message = "Payment method is required")
        PaymentMethod method
) {
}
