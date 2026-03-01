package com.trendyolclone.payment.application.service;

import com.trendyolclone.common.exception.BusinessRuleException;
import com.trendyolclone.common.exception.ResourceNotFoundException;
import com.trendyolclone.payment.application.dto.InitiatePaymentRequest;
import com.trendyolclone.payment.application.dto.PaymentResponse;
import com.trendyolclone.payment.domain.event.PaymentCompletedEvent;
import com.trendyolclone.payment.domain.event.PaymentFailedEvent;
import com.trendyolclone.payment.domain.model.Payment;
import com.trendyolclone.payment.domain.model.PaymentMethod;
import com.trendyolclone.payment.domain.model.PaymentStatus;
import com.trendyolclone.payment.domain.repository.PaymentRepository;
import com.trendyolclone.payment.domain.service.PaymentGateway;
import com.trendyolclone.payment.domain.service.PaymentResult;
import com.trendyolclone.payment.infrastructure.kafka.PaymentEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService Unit Tests")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private PaymentEventProducer paymentEventProducer;

    @InjectMocks
    private PaymentService paymentService;

    private UUID userId;
    private UUID paymentId;
    private String orderNumber;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        paymentId = UUID.randomUUID();
        orderNumber = "TY-20260301-ABC123";
    }

    private Payment createTestPayment(PaymentStatus status) {
        Payment payment = Payment.builder()
                .orderNumber(orderNumber)
                .userId(userId)
                .amount(BigDecimal.valueOf(150))
                .currency("TRY")
                .method(PaymentMethod.CREDIT_CARD)
                .status(status)
                .build();
        payment.setId(paymentId);
        return payment;
    }

    private InitiatePaymentRequest createPaymentRequest() {
        return new InitiatePaymentRequest(
                orderNumber,
                userId,
                BigDecimal.valueOf(150),
                PaymentMethod.CREDIT_CARD
        );
    }

    @Nested
    @DisplayName("initiatePayment")
    class InitiatePaymentTests {

        @Test
        @DisplayName("Should initiate payment successfully when gateway returns success")
        void shouldInitiatePaymentSuccessfully() {
            // given
            InitiatePaymentRequest request = createPaymentRequest();
            when(paymentRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.empty());
            when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
                Payment saved = invocation.getArgument(0);
                if (saved.getId() == null) {
                    saved.setId(paymentId);
                }
                return saved;
            });

            PaymentResult successResult = new PaymentResult(true, "TXN-abc12345", null);
            when(paymentGateway.processPayment(eq(paymentId), eq(BigDecimal.valueOf(150)), eq(PaymentMethod.CREDIT_CARD)))
                    .thenReturn(successResult);

            // when
            PaymentResponse response = paymentService.initiatePayment(request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.status()).isEqualTo(PaymentStatus.COMPLETED);
            assertThat(response.transactionId()).isEqualTo("TXN-abc12345");
            assertThat(response.orderNumber()).isEqualTo(orderNumber);

            verify(paymentEventProducer).publishPaymentCompleted(any(PaymentCompletedEvent.class));
            verify(paymentEventProducer, never()).publishPaymentFailed(any(PaymentFailedEvent.class));
        }

        @Test
        @DisplayName("Should set FAILED status when gateway returns failure")
        void shouldSetFailedStatusWhenGatewayFails() {
            // given
            InitiatePaymentRequest request = createPaymentRequest();
            when(paymentRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.empty());
            when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
                Payment saved = invocation.getArgument(0);
                if (saved.getId() == null) {
                    saved.setId(paymentId);
                }
                return saved;
            });

            PaymentResult failureResult = new PaymentResult(false, null, "Payment declined by bank");
            when(paymentGateway.processPayment(eq(paymentId), eq(BigDecimal.valueOf(150)), eq(PaymentMethod.CREDIT_CARD)))
                    .thenReturn(failureResult);

            // when
            PaymentResponse response = paymentService.initiatePayment(request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.status()).isEqualTo(PaymentStatus.FAILED);
            assertThat(response.failureReason()).isEqualTo("Payment declined by bank");
            assertThat(response.transactionId()).isNull();

            verify(paymentEventProducer).publishPaymentFailed(any(PaymentFailedEvent.class));
            verify(paymentEventProducer, never()).publishPaymentCompleted(any(PaymentCompletedEvent.class));
        }

        @Test
        @DisplayName("Should publish PaymentCompletedEvent with correct data on success")
        void shouldPublishPaymentCompletedEventWithCorrectData() {
            // given
            InitiatePaymentRequest request = createPaymentRequest();
            when(paymentRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.empty());
            when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
                Payment saved = invocation.getArgument(0);
                if (saved.getId() == null) {
                    saved.setId(paymentId);
                }
                return saved;
            });

            PaymentResult successResult = new PaymentResult(true, "TXN-12345678", null);
            when(paymentGateway.processPayment(any(), any(), any())).thenReturn(successResult);

            // when
            paymentService.initiatePayment(request);

            // then
            ArgumentCaptor<PaymentCompletedEvent> eventCaptor = ArgumentCaptor.forClass(PaymentCompletedEvent.class);
            verify(paymentEventProducer).publishPaymentCompleted(eventCaptor.capture());

            PaymentCompletedEvent event = eventCaptor.getValue();
            assertThat(event.paymentId()).isEqualTo(paymentId);
            assertThat(event.orderNumber()).isEqualTo(orderNumber);
            assertThat(event.userId()).isEqualTo(userId);
            assertThat(event.amount()).isEqualByComparingTo(BigDecimal.valueOf(150));
            assertThat(event.transactionId()).isEqualTo("TXN-12345678");
        }

        @Test
        @DisplayName("Should return existing payment for idempotency when non-failed payment exists")
        void shouldReturnExistingPaymentForIdempotency() {
            // given
            InitiatePaymentRequest request = createPaymentRequest();
            Payment existingPayment = createTestPayment(PaymentStatus.COMPLETED);
            existingPayment.setTransactionId("TXN-existing");
            when(paymentRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(existingPayment));

            // when
            PaymentResponse response = paymentService.initiatePayment(request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(paymentId);
            assertThat(response.status()).isEqualTo(PaymentStatus.COMPLETED);
            assertThat(response.transactionId()).isEqualTo("TXN-existing");

            // Should NOT create a new payment or call the gateway
            verify(paymentGateway, never()).processPayment(any(), any(), any());
            verify(paymentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should create new payment when existing payment has FAILED status")
        void shouldCreateNewPaymentWhenExistingIsFailed() {
            // given
            InitiatePaymentRequest request = createPaymentRequest();
            Payment failedPayment = createTestPayment(PaymentStatus.FAILED);
            when(paymentRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(failedPayment));
            when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
                Payment saved = invocation.getArgument(0);
                if (saved.getId() == null) {
                    saved.setId(UUID.randomUUID());
                }
                return saved;
            });

            PaymentResult successResult = new PaymentResult(true, "TXN-retry123", null);
            when(paymentGateway.processPayment(any(), any(), any())).thenReturn(successResult);

            // when
            PaymentResponse response = paymentService.initiatePayment(request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.status()).isEqualTo(PaymentStatus.COMPLETED);
            verify(paymentGateway).processPayment(any(), any(), any());
        }

        @Test
        @DisplayName("Should publish PaymentFailedEvent with correct data on failure")
        void shouldPublishPaymentFailedEventWithCorrectData() {
            // given
            InitiatePaymentRequest request = createPaymentRequest();
            when(paymentRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.empty());
            when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
                Payment saved = invocation.getArgument(0);
                if (saved.getId() == null) {
                    saved.setId(paymentId);
                }
                return saved;
            });

            PaymentResult failureResult = new PaymentResult(false, null, "Insufficient funds");
            when(paymentGateway.processPayment(any(), any(), any())).thenReturn(failureResult);

            // when
            paymentService.initiatePayment(request);

            // then
            ArgumentCaptor<PaymentFailedEvent> eventCaptor = ArgumentCaptor.forClass(PaymentFailedEvent.class);
            verify(paymentEventProducer).publishPaymentFailed(eventCaptor.capture());

            PaymentFailedEvent event = eventCaptor.getValue();
            assertThat(event.paymentId()).isEqualTo(paymentId);
            assertThat(event.orderNumber()).isEqualTo(orderNumber);
            assertThat(event.userId()).isEqualTo(userId);
            assertThat(event.reason()).isEqualTo("Insufficient funds");
        }
    }

    @Nested
    @DisplayName("getPayment")
    class GetPaymentTests {

        @Test
        @DisplayName("Should return payment when found by id")
        void shouldReturnPaymentWhenFound() {
            // given
            Payment payment = createTestPayment(PaymentStatus.COMPLETED);
            payment.setTransactionId("TXN-abc");
            when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

            // when
            PaymentResponse response = paymentService.getPayment(paymentId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(paymentId);
            assertThat(response.orderNumber()).isEqualTo(orderNumber);
            assertThat(response.status()).isEqualTo(PaymentStatus.COMPLETED);
            assertThat(response.transactionId()).isEqualTo("TXN-abc");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when payment not found by id")
        void shouldThrowWhenPaymentNotFoundById() {
            // given
            when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> paymentService.getPayment(paymentId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Payment")
                    .hasMessageContaining("id");
        }
    }

    @Nested
    @DisplayName("getPaymentByOrderNumber")
    class GetPaymentByOrderNumberTests {

        @Test
        @DisplayName("Should return payment when found by order number")
        void shouldReturnPaymentWhenFoundByOrderNumber() {
            // given
            Payment payment = createTestPayment(PaymentStatus.PENDING);
            when(paymentRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(payment));

            // when
            PaymentResponse response = paymentService.getPaymentByOrderNumber(orderNumber);

            // then
            assertThat(response).isNotNull();
            assertThat(response.orderNumber()).isEqualTo(orderNumber);
            assertThat(response.status()).isEqualTo(PaymentStatus.PENDING);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when payment not found by order number")
        void shouldThrowWhenPaymentNotFoundByOrderNumber() {
            // given
            when(paymentRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> paymentService.getPaymentByOrderNumber(orderNumber))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Payment")
                    .hasMessageContaining("orderNumber");
        }
    }

    @Nested
    @DisplayName("refundPayment")
    class RefundPaymentTests {

        @Test
        @DisplayName("Should refund payment successfully from COMPLETED status")
        void shouldRefundPaymentFromCompletedStatus() {
            // given
            Payment payment = createTestPayment(PaymentStatus.COMPLETED);
            payment.setTransactionId("TXN-refund123");
            when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
            when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            PaymentResponse response = paymentService.refundPayment(paymentId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.status()).isEqualTo(PaymentStatus.REFUNDED);
            verify(paymentEventProducer).publishPaymentRefunded(payment);
            verify(paymentRepository).save(payment);
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when refunding PENDING payment")
        void shouldThrowWhenRefundingPendingPayment() {
            // given
            Payment payment = createTestPayment(PaymentStatus.PENDING);
            when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

            // when/then
            assertThatThrownBy(() -> paymentService.refundPayment(paymentId))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Only completed payments can be refunded")
                    .hasMessageContaining("PENDING");
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when refunding FAILED payment")
        void shouldThrowWhenRefundingFailedPayment() {
            // given
            Payment payment = createTestPayment(PaymentStatus.FAILED);
            when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

            // when/then
            assertThatThrownBy(() -> paymentService.refundPayment(paymentId))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Only completed payments can be refunded")
                    .hasMessageContaining("FAILED");
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when refunding PROCESSING payment")
        void shouldThrowWhenRefundingProcessingPayment() {
            // given
            Payment payment = createTestPayment(PaymentStatus.PROCESSING);
            when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

            // when/then
            assertThatThrownBy(() -> paymentService.refundPayment(paymentId))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Only completed payments can be refunded");
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when refunding already REFUNDED payment")
        void shouldThrowWhenRefundingAlreadyRefundedPayment() {
            // given
            Payment payment = createTestPayment(PaymentStatus.REFUNDED);
            when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

            // when/then
            assertThatThrownBy(() -> paymentService.refundPayment(paymentId))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Only completed payments can be refunded")
                    .hasMessageContaining("REFUNDED");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when payment not found for refund")
        void shouldThrowWhenPaymentNotFoundForRefund() {
            // given
            when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> paymentService.refundPayment(paymentId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
