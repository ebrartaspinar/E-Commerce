package com.ecommerce.payment.domain.service;

import com.ecommerce.payment.domain.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentGateway {

    PaymentResult processPayment(UUID paymentId, BigDecimal amount, PaymentMethod method);
}
