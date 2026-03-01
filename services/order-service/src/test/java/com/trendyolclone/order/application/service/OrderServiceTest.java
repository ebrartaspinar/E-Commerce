package com.trendyolclone.order.application.service;

import com.trendyolclone.common.dto.PagedResponse;
import com.trendyolclone.common.exception.BusinessRuleException;
import com.trendyolclone.common.exception.ResourceNotFoundException;
import com.trendyolclone.order.application.dto.CancelOrderRequest;
import com.trendyolclone.order.application.dto.CreateOrderRequest;
import com.trendyolclone.order.application.dto.OrderResponse;
import com.trendyolclone.order.domain.event.OrderCancelledEvent;
import com.trendyolclone.order.domain.event.OrderCreatedEvent;
import com.trendyolclone.order.domain.model.*;
import com.trendyolclone.order.domain.repository.OrderRepository;
import com.trendyolclone.order.infrastructure.kafka.OrderEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Unit Tests")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventProducer orderEventProducer;

    @InjectMocks
    private OrderService orderService;

    private UUID userId;
    private UUID orderId;
    private String orderNumber;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        orderNumber = "TY-20260301-ABC123";
    }

    private Order createTestOrder(OrderStatus status) {
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .userId(userId)
                .status(status)
                .shippingAddress(OrderAddress.builder()
                        .fullName("Test User")
                        .phone("5551234567")
                        .fullAddress("Test Address")
                        .city("Istanbul")
                        .district("Kadikoy")
                        .postalCode("34000")
                        .build())
                .totalAmount(BigDecimal.valueOf(100))
                .discountAmount(BigDecimal.ZERO)
                .shippingCost(BigDecimal.ZERO)
                .finalAmount(BigDecimal.valueOf(100))
                .build();
        order.setId(orderId);

        OrderItem item = OrderItem.builder()
                .productId(UUID.randomUUID())
                .sellerId(UUID.randomUUID())
                .productName("Test Product")
                .unitPrice(BigDecimal.valueOf(100))
                .quantity(1)
                .totalPrice(BigDecimal.valueOf(100))
                .status(OrderItemStatus.PENDING)
                .build();
        item.setId(UUID.randomUUID());
        order.addItem(item);

        return order;
    }

    @Nested
    @DisplayName("createOrder")
    class CreateOrderTests {

        @Test
        @DisplayName("Should create order with generated order number and CREATED status")
        void shouldCreateOrderWithGeneratedOrderNumberAndCreatedStatus() {
            // given
            CreateOrderRequest request = new CreateOrderRequest(UUID.randomUUID(), "Please deliver fast");
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
                Order saved = invocation.getArgument(0);
                saved.setId(orderId);
                return saved;
            });

            // when
            OrderResponse response = orderService.createOrder(userId, request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.orderNumber()).startsWith("TY-");
            assertThat(response.status()).isEqualTo(OrderStatus.CREATED);
            assertThat(response.userId()).isEqualTo(userId);

            verify(orderRepository).save(any(Order.class));
        }

        @Test
        @DisplayName("Should publish OrderCreatedEvent after successful creation")
        void shouldPublishOrderCreatedEventAfterCreation() {
            // given
            CreateOrderRequest request = new CreateOrderRequest(UUID.randomUUID(), null);
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
                Order saved = invocation.getArgument(0);
                saved.setId(orderId);
                return saved;
            });

            // when
            orderService.createOrder(userId, request);

            // then
            ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
            verify(orderEventProducer).publishOrderCreated(eventCaptor.capture());

            OrderCreatedEvent event = eventCaptor.getValue();
            assertThat(event.orderId()).isEqualTo(orderId);
            assertThat(event.userId()).isEqualTo(userId);
            assertThat(event.itemCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should generate order number in expected format TY-yyyyMMdd-XXXXXX")
        void shouldGenerateOrderNumberInExpectedFormat() {
            // given
            CreateOrderRequest request = new CreateOrderRequest(UUID.randomUUID(), null);
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
                Order saved = invocation.getArgument(0);
                saved.setId(orderId);
                return saved;
            });

            // when
            OrderResponse response = orderService.createOrder(userId, request);

            // then
            assertThat(response.orderNumber()).matches("TY-\\d{8}-[A-Z0-9]{6}");
        }
    }

    @Nested
    @DisplayName("getOrder")
    class GetOrderTests {

        @Test
        @DisplayName("Should return order when found and user matches")
        void shouldReturnOrderWhenFoundAndUserMatches() {
            // given
            Order order = createTestOrder(OrderStatus.CREATED);
            when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(order));

            // when
            OrderResponse response = orderService.getOrder(userId, orderNumber);

            // then
            assertThat(response).isNotNull();
            assertThat(response.orderNumber()).isEqualTo(orderNumber);
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.status()).isEqualTo(OrderStatus.CREATED);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when order not found")
        void shouldThrowResourceNotFoundExceptionWhenOrderNotFound() {
            // given
            when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> orderService.getOrder(userId, orderNumber))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Order")
                    .hasMessageContaining(orderNumber);
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when user does not own the order")
        void shouldThrowBusinessRuleExceptionWhenUserDoesNotOwnOrder() {
            // given
            Order order = createTestOrder(OrderStatus.CREATED);
            UUID differentUserId = UUID.randomUUID();
            when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(order));

            // when/then
            assertThatThrownBy(() -> orderService.getOrder(differentUserId, orderNumber))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("permission");
        }
    }

    @Nested
    @DisplayName("getOrders")
    class GetOrdersTests {

        @Test
        @DisplayName("Should return paged results for user")
        void shouldReturnPagedResultsForUser() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Order order = createTestOrder(OrderStatus.CREATED);
            Page<Order> orderPage = new PageImpl<>(List.of(order), pageable, 1);
            when(orderRepository.findByUserId(userId, pageable)).thenReturn(orderPage);

            // when
            PagedResponse<OrderResponse> response = orderService.getOrders(userId, pageable);

            // then
            assertThat(response).isNotNull();
            assertThat(response.content()).hasSize(1);
            assertThat(response.totalElements()).isEqualTo(1);
            assertThat(response.page()).isEqualTo(0);
            assertThat(response.size()).isEqualTo(10);
        }

        @Test
        @DisplayName("Should return empty paged results when no orders exist")
        void shouldReturnEmptyPagedResultsWhenNoOrders() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Order> emptyPage = new PageImpl<>(List.of(), pageable, 0);
            when(orderRepository.findByUserId(userId, pageable)).thenReturn(emptyPage);

            // when
            PagedResponse<OrderResponse> response = orderService.getOrders(userId, pageable);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("cancelOrder")
    class CancelOrderTests {

        @Test
        @DisplayName("Should cancel order successfully from CREATED status")
        void shouldCancelOrderFromCreatedStatus() {
            // given
            Order order = createTestOrder(OrderStatus.CREATED);
            CancelOrderRequest request = new CancelOrderRequest("Changed my mind");
            when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            OrderResponse response = orderService.cancelOrder(userId, orderNumber, request);

            // then
            assertThat(response.status()).isEqualTo(OrderStatus.CANCELLED);
            verify(orderRepository).save(any(Order.class));
        }

        @Test
        @DisplayName("Should cancel order successfully from PAYMENT_PENDING status")
        void shouldCancelOrderFromPaymentPendingStatus() {
            // given
            Order order = createTestOrder(OrderStatus.PAYMENT_PENDING);
            CancelOrderRequest request = new CancelOrderRequest("Too expensive");
            when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            OrderResponse response = orderService.cancelOrder(userId, orderNumber, request);

            // then
            assertThat(response.status()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        @DisplayName("Should publish OrderCancelledEvent after cancellation")
        void shouldPublishOrderCancelledEventAfterCancellation() {
            // given
            Order order = createTestOrder(OrderStatus.CREATED);
            CancelOrderRequest request = new CancelOrderRequest("No longer needed");
            when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            orderService.cancelOrder(userId, orderNumber, request);

            // then
            ArgumentCaptor<OrderCancelledEvent> eventCaptor = ArgumentCaptor.forClass(OrderCancelledEvent.class);
            verify(orderEventProducer).publishOrderCancelled(eventCaptor.capture());

            OrderCancelledEvent event = eventCaptor.getValue();
            assertThat(event.orderId()).isEqualTo(orderId);
            assertThat(event.reason()).isEqualTo("No longer needed");
        }

        @Test
        @DisplayName("Should cancel all pending and confirmed items when order is cancelled")
        void shouldCancelAllPendingAndConfirmedItems() {
            // given
            Order order = createTestOrder(OrderStatus.CREATED);
            // Add a confirmed item
            OrderItem confirmedItem = OrderItem.builder()
                    .productId(UUID.randomUUID())
                    .sellerId(UUID.randomUUID())
                    .productName("Confirmed Product")
                    .unitPrice(BigDecimal.valueOf(50))
                    .quantity(1)
                    .totalPrice(BigDecimal.valueOf(50))
                    .status(OrderItemStatus.CONFIRMED)
                    .build();
            confirmedItem.setId(UUID.randomUUID());
            order.addItem(confirmedItem);

            CancelOrderRequest request = new CancelOrderRequest("Cancel all");
            when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            orderService.cancelOrder(userId, orderNumber, request);

            // then
            assertThat(order.getItems()).allMatch(item -> item.getStatus() == OrderItemStatus.CANCELLED);
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when cancelling order from DELIVERED status")
        void shouldThrowWhenCancellingDeliveredOrder() {
            // given
            Order order = createTestOrder(OrderStatus.DELIVERED);
            CancelOrderRequest request = new CancelOrderRequest("Want to cancel");
            when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(order));

            // when/then
            assertThatThrownBy(() -> orderService.cancelOrder(userId, orderNumber, request))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Cannot transition");
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when cancelling order from SHIPPED status")
        void shouldThrowWhenCancellingShippedOrder() {
            // given
            Order order = createTestOrder(OrderStatus.SHIPPED);
            CancelOrderRequest request = new CancelOrderRequest("Want to cancel");
            when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(order));

            // when/then
            assertThatThrownBy(() -> orderService.cancelOrder(userId, orderNumber, request))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Cannot transition");
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when user does not own the order")
        void shouldThrowWhenUserDoesNotOwnOrder() {
            // given
            Order order = createTestOrder(OrderStatus.CREATED);
            UUID differentUserId = UUID.randomUUID();
            CancelOrderRequest request = new CancelOrderRequest("Cancel");
            when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(order));

            // when/then
            assertThatThrownBy(() -> orderService.cancelOrder(differentUserId, orderNumber, request))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("permission");
        }
    }

    @Nested
    @DisplayName("updatePaymentStatus")
    class UpdatePaymentStatusTests {

        @Test
        @DisplayName("Should transition order to PAYMENT_PENDING from CREATED")
        void shouldTransitionToPaymentPending() {
            // given
            Order order = createTestOrder(OrderStatus.CREATED);
            UUID paymentId = UUID.randomUUID();
            when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            orderService.updatePaymentStatus(orderNumber, OrderStatus.PAYMENT_PENDING, paymentId);

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_PENDING);
            assertThat(order.getPaymentId()).isEqualTo(paymentId);
            verify(orderRepository).save(order);
        }

        @Test
        @DisplayName("Should transition order to PAYMENT_COMPLETED from PAYMENT_PENDING")
        void shouldTransitionToPaymentCompleted() {
            // given
            Order order = createTestOrder(OrderStatus.PAYMENT_PENDING);
            UUID paymentId = UUID.randomUUID();
            when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            orderService.updatePaymentStatus(orderNumber, OrderStatus.PAYMENT_COMPLETED, paymentId);

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_COMPLETED);
            assertThat(order.getPaymentId()).isEqualTo(paymentId);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when order not found")
        void shouldThrowWhenOrderNotFound() {
            // given
            when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> orderService.updatePaymentStatus(orderNumber, OrderStatus.PAYMENT_PENDING, UUID.randomUUID()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Should throw BusinessRuleException for invalid state transition")
        void shouldThrowForInvalidTransition() {
            // given
            Order order = createTestOrder(OrderStatus.DELIVERED);
            when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(order));

            // when/then
            assertThatThrownBy(() -> orderService.updatePaymentStatus(orderNumber, OrderStatus.PAYMENT_PENDING, UUID.randomUUID()))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Cannot transition");
        }
    }
}
