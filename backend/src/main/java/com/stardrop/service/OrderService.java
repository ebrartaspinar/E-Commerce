package com.stardrop.service;

import com.stardrop.dto.OrderRequest;
import com.stardrop.event.OrderEvent;
import com.stardrop.event.OrderEventProducer;
import com.stardrop.model.*;
import com.stardrop.repository.CartItemRepository;
import com.stardrop.repository.OrderRepository;
import com.stardrop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderEventProducer orderEventProducer;

    @Transactional
    public Order createOrder(Long userId, OrderRequest request) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Calculate total and validate stock
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        // Create order
        Order order = Order.builder()
                .userId(userId)
                .status(Order.OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .shippingAddress(request.shippingAddress())
                .orderItems(new ArrayList<>())
                .build();

        // Create order items and reduce stock
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(product.getId())
                    .productName(product.getName())
                    .price(product.getPrice())
                    .quantity(cartItem.getQuantity())
                    .build();

            order.getOrderItems().add(orderItem);

            // Reduce stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        Order savedOrder = orderRepository.save(order);

        // Clear cart
        cartItemRepository.deleteByUserId(userId);

        // Kafka event gonder
        orderEventProducer.sendOrderEvent(OrderEvent.builder()
                .orderId(savedOrder.getId())
                .userId(userId)
                .status("CREATED")
                .totalAmount(totalAmount)
                .itemCount(cartItems.size())
                .createdAt(LocalDateTime.now())
                .build());

        return savedOrder;
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Order getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("This order does not belong to you");
        }

        return order;
    }

    @Transactional
    public Order cancelOrder(Long userId, Long orderId) {
        Order order = getOrderById(userId, orderId);

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Only pending orders can be cancelled");
        }

        // Restore stock
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = productRepository.findById(orderItem.getProductId())
                    .orElse(null);
            if (product != null) {
                product.setStock(product.getStock() + orderItem.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);

        // Kafka event gonder
        orderEventProducer.sendOrderEvent(OrderEvent.builder()
                .orderId(cancelledOrder.getId())
                .userId(userId)
                .status("CANCELLED")
                .totalAmount(cancelledOrder.getTotalAmount())
                .itemCount(cancelledOrder.getOrderItems().size())
                .createdAt(LocalDateTime.now())
                .build());

        return cancelledOrder;
    }
}
