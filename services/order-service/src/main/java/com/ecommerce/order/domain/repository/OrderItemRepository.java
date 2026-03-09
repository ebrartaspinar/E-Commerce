package com.ecommerce.order.domain.repository;

import com.ecommerce.order.domain.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    List<OrderItem> findBySellerId(UUID sellerId);
}
