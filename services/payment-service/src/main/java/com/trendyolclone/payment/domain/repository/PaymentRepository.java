package com.trendyolclone.payment.domain.repository;

import com.trendyolclone.payment.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByOrderNumber(String orderNumber);

    List<Payment> findByUserId(UUID userId);
}
