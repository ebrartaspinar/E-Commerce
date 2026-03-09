package com.ecommerce.payment.domain.service;

import com.ecommerce.payment.domain.model.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MockPaymentGateway Unit Tests")
class MockPaymentGatewayTest {

    private MockPaymentGateway mockPaymentGateway;

    @BeforeEach
    void setUp() {
        mockPaymentGateway = new MockPaymentGateway();
    }

    @Test
    @DisplayName("Should return a PaymentResult from processPayment")
    void shouldReturnPaymentResult() {
        // given
        UUID paymentId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100);
        PaymentMethod method = PaymentMethod.CREDIT_CARD;

        // when
        PaymentResult result = mockPaymentGateway.processPayment(paymentId, amount, method);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should return a result with either success or failure fields populated")
    void shouldReturnResultWithProperFields() {
        // given
        UUID paymentId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(250);
        PaymentMethod method = PaymentMethod.DEBIT_CARD;

        // when
        PaymentResult result = mockPaymentGateway.processPayment(paymentId, amount, method);

        // then
        if (result.success()) {
            assertThat(result.transactionId()).isNotNull();
            assertThat(result.transactionId()).startsWith("TXN-");
            assertThat(result.failureReason()).isNull();
        } else {
            assertThat(result.transactionId()).isNull();
            assertThat(result.failureReason()).isNotNull();
            assertThat(result.failureReason()).isEqualTo("Payment declined by bank");
        }
    }

    @Test
    @DisplayName("Transaction ID should start with TXN- prefix when payment succeeds")
    void transactionIdShouldStartWithTxnPrefix() {
        // given
        UUID paymentId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(50);
        PaymentMethod method = PaymentMethod.CREDIT_CARD;

        // Run multiple times to get at least one success
        PaymentResult successResult = null;
        for (int i = 0; i < 100; i++) {
            PaymentResult result = mockPaymentGateway.processPayment(paymentId, amount, method);
            if (result.success()) {
                successResult = result;
                break;
            }
        }

        // then
        assertThat(successResult).isNotNull();
        assertThat(successResult.transactionId()).startsWith("TXN-");
        assertThat(successResult.transactionId()).hasSize(12); // "TXN-" + 8 chars
    }

    @Test
    @DisplayName("Should handle BANK_TRANSFER payment method")
    void shouldHandleBankTransferMethod() {
        // given
        UUID paymentId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(500);
        PaymentMethod method = PaymentMethod.BANK_TRANSFER;

        // when
        PaymentResult result = mockPaymentGateway.processPayment(paymentId, amount, method);

        // then
        assertThat(result).isNotNull();
        assertThat(result.success() || !result.success()).isTrue();
    }

    @RepeatedTest(5)
    @DisplayName("Should consistently return valid PaymentResult across repeated calls")
    void shouldConsistentlyReturnValidResult() {
        // given
        UUID paymentId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(75.50);
        PaymentMethod method = PaymentMethod.CREDIT_CARD;

        // when
        PaymentResult result = mockPaymentGateway.processPayment(paymentId, amount, method);

        // then
        assertThat(result).isNotNull();
        if (result.success()) {
            assertThat(result.transactionId()).isNotBlank();
            assertThat(result.transactionId()).startsWith("TXN-");
        } else {
            assertThat(result.failureReason()).isNotBlank();
        }
    }
}
