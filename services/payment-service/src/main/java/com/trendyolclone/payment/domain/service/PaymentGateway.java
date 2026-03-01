package com.trendyolclone.payment.domain.service;

import com.trendyolclone.payment.domain.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentGateway {

    PaymentResult processPayment(UUID paymentId, BigDecimal amount, PaymentMethod method);
}
