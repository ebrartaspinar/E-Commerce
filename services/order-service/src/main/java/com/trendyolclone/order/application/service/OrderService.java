package com.trendyolclone.order.application.service;

import com.trendyolclone.common.dto.PagedResponse;
import com.trendyolclone.common.exception.BusinessRuleException;
import com.trendyolclone.common.exception.ResourceNotFoundException;
import com.trendyolclone.order.application.dto.*;
import com.trendyolclone.order.domain.event.OrderCancelledEvent;
import com.trendyolclone.order.domain.event.OrderCreatedEvent;
import com.trendyolclone.order.domain.model.*;
import com.trendyolclone.order.domain.repository.OrderRepository;
import com.trendyolclone.order.infrastructure.kafka.OrderEventProducer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    @Transactional
    @CacheEvict(value = "order-lists", allEntries = true)
    public OrderResponse createOrder(UUID userId, CreateOrderRequest request) {
        String orderNumber = generateOrderNumber();

        OrderAddress shippingAddress = OrderAddress.builder()
                .fullName("Placeholder Name")
                .phone("5551234567")
                .fullAddress("Placeholder Address")
                .city("Istanbul")
                .district("Kadikoy")
                .postalCode("34000")
                .build();

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .userId(userId)
                .status(OrderStatus.CREATED)
                .shippingAddress(shippingAddress)
                .totalAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .shippingCost(BigDecimal.ZERO)
                .finalAmount(BigDecimal.ZERO)
                .notes(request.notes())
                .build();

        // Create a dummy order item for now (will be populated from cart service later)
        OrderItem dummyItem = OrderItem.builder()
                .productId(UUID.randomUUID())
                .sellerId(UUID.randomUUID())
                .productName("Placeholder Product")
                .productImage(null)
                .unitPrice(BigDecimal.valueOf(100))
                .quantity(1)
                .totalPrice(BigDecimal.valueOf(100))
                .status(OrderItemStatus.PENDING)
                .build();

        order.addItem(dummyItem);
        order.setTotalAmount(dummyItem.getTotalPrice());
        order.setFinalAmount(dummyItem.getTotalPrice());

        Order savedOrder = orderRepository.save(order);
        log.info("Created order orderNumber={} for userId={}", orderNumber, userId);

        // Publish order created event
        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getOrderNumber(),
                savedOrder.getUserId(),
                savedOrder.getFinalAmount(),
                savedOrder.getItems().size()
        );
        orderEventProducer.publishOrderCreated(event);

        return toOrderResponse(savedOrder);
    }

    @Cacheable(value = "orders", key = "#orderNumber")
    public OrderResponse getOrder(UUID userId, String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));

        if (!order.getUserId().equals(userId)) {
            throw new BusinessRuleException("You do not have permission to view this order");
        }

        return toOrderResponse(order);
    }

    @Cacheable(value = "order-lists", key = "#userId + ':page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public PagedResponse<OrderResponse> getOrders(UUID userId, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByUserId(userId, pageable);
        Page<OrderResponse> responsePage = orderPage.map(this::toOrderResponse);
        return PagedResponse.from(responsePage);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "orders", key = "#orderNumber"),
            @CacheEvict(value = "order-lists", allEntries = true)
    })
    public OrderResponse cancelOrder(UUID userId, String orderNumber, CancelOrderRequest request) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));

        if (!order.getUserId().equals(userId)) {
            throw new BusinessRuleException("You do not have permission to cancel this order");
        }

        order.transitionTo(OrderStatus.CANCELLED);
        order.setCancelReason(request.reason());

        // Cancel all pending items
        order.getItems().forEach(item -> {
            if (item.getStatus() == OrderItemStatus.PENDING || item.getStatus() == OrderItemStatus.CONFIRMED) {
                item.setStatus(OrderItemStatus.CANCELLED);
            }
        });

        Order savedOrder = orderRepository.save(order);
        log.info("Cancelled order orderNumber={} for userId={}, reason={}", orderNumber, userId, request.reason());

        // Publish order cancelled event
        OrderCancelledEvent event = new OrderCancelledEvent(
                savedOrder.getId(),
                savedOrder.getOrderNumber(),
                savedOrder.getUserId(),
                request.reason()
        );
        orderEventProducer.publishOrderCancelled(event);

        return toOrderResponse(savedOrder);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "orders", key = "#orderNumber"),
            @CacheEvict(value = "order-lists", allEntries = true)
    })
    public void updatePaymentStatus(String orderNumber, OrderStatus newStatus, UUID paymentId) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));

        order.transitionTo(newStatus);
        order.setPaymentId(paymentId);

        orderRepository.save(order);
        log.info("Updated payment status for orderNumber={} to {} with paymentId={}",
                orderNumber, newStatus, paymentId);
    }

    private String generateOrderNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        StringBuilder randomPart = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            randomPart.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return "TY-" + datePart + "-" + randomPart;
    }

    private OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::toOrderItemResponse)
                .toList();

        OrderAddressResponse addressResponse = null;
        if (order.getShippingAddress() != null) {
            OrderAddress addr = order.getShippingAddress();
            addressResponse = new OrderAddressResponse(
                    addr.getFullName(),
                    addr.getPhone(),
                    addr.getFullAddress(),
                    addr.getCity(),
                    addr.getDistrict(),
                    addr.getPostalCode()
            );
        }

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getUserId(),
                order.getStatus(),
                itemResponses,
                addressResponse,
                order.getTotalAmount(),
                order.getDiscountAmount(),
                order.getShippingCost(),
                order.getFinalAmount(),
                order.getCreatedAt()
        );
    }

    private OrderItemResponse toOrderItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProductId(),
                item.getSellerId(),
                item.getProductName(),
                item.getProductImage(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getTotalPrice(),
                item.getStatus()
        );
    }
}
