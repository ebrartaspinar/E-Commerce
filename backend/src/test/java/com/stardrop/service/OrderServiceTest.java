package com.stardrop.service;

import com.stardrop.dto.OrderRequest;
import com.stardrop.event.OrderEventProducer;
import com.stardrop.model.*;
import com.stardrop.repository.CartItemRepository;
import com.stardrop.repository.OrderRepository;
import com.stardrop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private ProductRepository productRepository;
    @Mock private OrderEventProducer orderEventProducer;

    @InjectMocks
    private OrderService orderService;

    private Product product;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L).name("Starfruit").price(new BigDecimal("1875"))
                .stock(50).build();

        cartItem = CartItem.builder()
                .id(1L).userId(1L).product(product).quantity(2).build();
    }

    @Test
    @DisplayName("createOrder - should create order from cart items")
    void createOrder_success() {
        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order o = i.getArgument(0);
            o.setId(1L);
            return o;
        });

        Order order = orderService.createOrder(1L, new OrderRequest("Pelican Town"));

        assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("3750"));
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
        assertThat(order.getOrderItems()).hasSize(1);
        assertThat(product.getStock()).isEqualTo(48); // 50 - 2
        verify(cartItemRepository).deleteByUserId(1L);
        verify(orderEventProducer).sendOrderEvent(any());
    }

    @Test
    @DisplayName("createOrder - should throw when cart is empty")
    void createOrder_emptyCart() {
        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of());

        assertThatThrownBy(() -> orderService.createOrder(1L, new OrderRequest("addr")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("empty");
    }

    @Test
    @DisplayName("createOrder - should throw when not enough stock")
    void createOrder_insufficientStock() {
        product.setStock(1); // only 1 in stock, but cart wants 2

        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem));

        assertThatThrownBy(() -> orderService.createOrder(1L, new OrderRequest("addr")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Not enough stock");
    }

    @Test
    @DisplayName("cancelOrder - should cancel and restore stock")
    void cancelOrder_success() {
        OrderItem orderItem = OrderItem.builder()
                .productId(1L).productName("Starfruit")
                .price(new BigDecimal("1875")).quantity(2).build();

        Order order = Order.builder()
                .id(1L).userId(1L).status(Order.OrderStatus.PENDING)
                .totalAmount(new BigDecimal("3750"))
                .orderItems(new ArrayList<>(List.of(orderItem))).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order cancelled = orderService.cancelOrder(1L, 1L);

        assertThat(cancelled.getStatus()).isEqualTo(Order.OrderStatus.CANCELLED);
        assertThat(product.getStock()).isEqualTo(52); // 50 + 2 restored
        verify(orderEventProducer).sendOrderEvent(any());
    }

    @Test
    @DisplayName("cancelOrder - should throw if not PENDING")
    void cancelOrder_notPending() {
        Order order = Order.builder()
                .id(1L).userId(1L).status(Order.OrderStatus.DELIVERED)
                .orderItems(new ArrayList<>()).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(1L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("pending");
    }

    @Test
    @DisplayName("getOrderById - should throw if order belongs to different user")
    void getOrderById_wrongUser() {
        Order order = Order.builder()
                .id(1L).userId(2L).status(Order.OrderStatus.PENDING)
                .orderItems(new ArrayList<>()).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.getOrderById(1L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("does not belong");
    }
}
