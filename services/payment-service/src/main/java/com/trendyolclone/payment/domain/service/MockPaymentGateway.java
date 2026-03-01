package com.trendyolclone.payment.domain.service;

import com.trendyolclone.payment.domain.model.PaymentMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

@Component
public class MockPaymentGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(MockPaymentGateway.class);
    private final Random random = new Random();

    @Override
    public PaymentResult processPayment(UUID paymentId, BigDecimal amount, PaymentMethod method) {
        log.info("Processing payment paymentId={}, amount={}, method={}", paymentId, amount, method);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new PaymentResult(false, null, "Payment processing interrupted");
        }

        boolean success = random.nextInt(100) < 90;

        if (success) {
            String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
            log.info("Payment successful paymentId={}, transactionId={}", paymentId, transactionId);
            return new PaymentResult(true, transactionId, null);
        } else {
            String failureReason = "Payment declined by bank";
            log.warn("Payment failed paymentId={}, reason={}", paymentId, failureReason);
            return new PaymentResult(false, null, failureReason);
        }
    }
}
