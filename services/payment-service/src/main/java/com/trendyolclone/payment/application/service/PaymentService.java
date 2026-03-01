package com.trendyolclone.payment.application.service;

import com.trendyolclone.common.exception.BusinessRuleException;
import com.trendyolclone.common.exception.ResourceNotFoundException;
import com.trendyolclone.payment.application.dto.InitiatePaymentRequest;
import com.trendyolclone.payment.application.dto.PaymentResponse;
import com.trendyolclone.payment.domain.event.PaymentCompletedEvent;
import com.trendyolclone.payment.domain.event.PaymentFailedEvent;
import com.trendyolclone.payment.domain.model.Payment;
import com.trendyolclone.payment.domain.model.PaymentStatus;
import com.trendyolclone.payment.domain.repository.PaymentRepository;
import com.trendyolclone.payment.domain.service.PaymentGateway;
import com.trendyolclone.payment.domain.service.PaymentResult;
import com.trendyolclone.payment.infrastructure.kafka.PaymentEventProducer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;
    private final PaymentEventProducer paymentEventProducer;

    @Transactional
    public PaymentResponse initiatePayment(InitiatePaymentRequest request) {
        log.info("Initiating payment for orderNumber={}", request.orderNumber());

        // Idempotency check
        Optional<Payment> existingPayment = paymentRepository.findByOrderNumber(request.orderNumber());
        if (existingPayment.isPresent() && existingPayment.get().getStatus() != PaymentStatus.FAILED) {
            log.info("Payment already exists for orderNumber={} with status={}",
                    request.orderNumber(), existingPayment.get().getStatus());
            return toResponse(existingPayment.get());
        }

        // Create new payment
        Payment payment = Payment.builder()
                .orderNumber(request.orderNumber())
                .userId(request.userId())
                .amount(request.amount())
                .currency("TRY")
                .method(request.method())
                .status(PaymentStatus.PENDING)
                .build();

        payment = paymentRepository.save(payment);
        log.info("Created payment id={} for orderNumber={}", payment.getId(), request.orderNumber());

        // Process payment via gateway
        payment.setStatus(PaymentStatus.PROCESSING);
        payment = paymentRepository.save(payment);

        PaymentResult result = paymentGateway.processPayment(payment.getId(), request.amount(), request.method());

        if (result.success()) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setTransactionId(result.transactionId());
            payment.setCompletedAt(LocalDateTime.now());
            payment = paymentRepository.save(payment);

            log.info("Payment completed paymentId={}, transactionId={}", payment.getId(), result.transactionId());

            paymentEventProducer.publishPaymentCompleted(new PaymentCompletedEvent(
                    payment.getId(),
                    payment.getOrderNumber(),
                    payment.getUserId(),
                    payment.getAmount(),
                    result.transactionId()
            ));
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(result.failureReason());
            payment = paymentRepository.save(payment);

            log.warn("Payment failed paymentId={}, reason={}", payment.getId(), result.failureReason());

            paymentEventProducer.publishPaymentFailed(new PaymentFailedEvent(
                    payment.getId(),
                    payment.getOrderNumber(),
                    payment.getUserId(),
                    result.failureReason()
            ));
        }

        return toResponse(payment);
    }

    public PaymentResponse getPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));
        return toResponse(payment);
    }

    public PaymentResponse getPaymentByOrderNumber(String orderNumber) {
        Payment payment = paymentRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "orderNumber", orderNumber));
        return toResponse(payment);
    }

    @Transactional
    public PaymentResponse refundPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new BusinessRuleException("Only completed payments can be refunded. Current status: " + payment.getStatus());
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment = paymentRepository.save(payment);

        log.info("Payment refunded paymentId={}, orderNumber={}", paymentId, payment.getOrderNumber());

        paymentEventProducer.publishPaymentRefunded(payment);

        return toResponse(payment);
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderNumber(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getTransactionId(),
                payment.getFailureReason(),
                payment.getCreatedAt(),
                payment.getCompletedAt()
        );
    }
}
