package com.ecommerce.order.application.service;

import com.ecommerce.common.dto.PagedResponse;
import com.ecommerce.common.exception.BusinessRuleException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.order.application.dto.OrderItemResponse;
import com.ecommerce.order.application.dto.OrderResponse;
import com.ecommerce.order.application.dto.OrderAddressResponse;
import com.ecommerce.order.application.dto.UpdateItemStatusRequest;
import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.model.OrderAddress;
import com.ecommerce.order.domain.model.OrderItem;
import com.ecommerce.order.domain.repository.OrderItemRepository;
import com.ecommerce.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerOrderService {

    private static final Logger log = LoggerFactory.getLogger(SellerOrderService.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getSellerOrders(UUID sellerId, Pageable pageable) {
        // Find all order items for this seller and get distinct orders
        List<OrderItem> sellerItems = orderItemRepository.findBySellerId(sellerId);
        List<Order> sellerOrders = sellerItems.stream()
                .map(OrderItem::getOrder)
                .distinct()
                .collect(Collectors.toList());

        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sellerOrders.size());
        List<Order> pageContent = start < sellerOrders.size()
                ? sellerOrders.subList(start, end)
                : List.of();

        Page<Order> orderPage = new PageImpl<>(pageContent, pageable, sellerOrders.size());
        Page<OrderResponse> responsePage = orderPage.map(this::toOrderResponse);
        return PagedResponse.from(responsePage);
    }

    @Transactional
    public OrderItemResponse updateItemStatus(UUID sellerId, String orderNumber, UUID itemId, UpdateItemStatusRequest request) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));

        OrderItem item = order.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem", "id", itemId));

        if (!item.getSellerId().equals(sellerId)) {
            throw new BusinessRuleException("You do not have permission to update this order item");
        }

        item.setStatus(request.status());
        orderRepository.save(order);

        log.info("Updated order item status: orderNumber={}, itemId={}, newStatus={}, sellerId={}",
                orderNumber, itemId, request.status(), sellerId);

        return toOrderItemResponse(item);
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
