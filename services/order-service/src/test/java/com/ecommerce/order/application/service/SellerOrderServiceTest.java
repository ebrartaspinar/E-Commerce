package com.ecommerce.order.application.service;

import com.ecommerce.common.dto.PagedResponse;
import com.ecommerce.common.exception.BusinessRuleException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.order.application.dto.OrderItemResponse;
import com.ecommerce.order.application.dto.OrderResponse;
import com.ecommerce.order.application.dto.UpdateItemStatusRequest;
import com.ecommerce.order.domain.model.*;
import com.ecommerce.order.domain.repository.OrderItemRepository;
import com.ecommerce.order.domain.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SellerOrderService Unit Tests")
class SellerOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private SellerOrderService sellerOrderService;

    private UUID sellerId;
    private UUID orderId;
    private UUID itemId;
    private String orderNumber;

    @BeforeEach
    void setUp() {
        sellerId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        itemId = UUID.randomUUID();
        orderNumber = "TY-20260301-ABC123";
    }

    private Order createTestOrderWithSellerItem(UUID sellerId) {
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .userId(UUID.randomUUID())
                .status(OrderStatus.CREATED)
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
                .sellerId(sellerId)
                .productName("Seller Product")
                .unitPrice(BigDecimal.valueOf(100))
                .quantity(1)
                .totalPrice(BigDecimal.valueOf(100))
                .status(OrderItemStatus.PENDING)
                .build();
        item.setId(itemId);
        order.addItem(item);

        return order;
    }

    @Nested
    @DisplayName("getSellerOrders")
    class GetSellerOrdersTests {

        @Test
        @DisplayName("Should return seller's order items as paged response")
        void shouldReturnSellerOrdersAsPagedResponse() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Order order = createTestOrderWithSellerItem(sellerId);
            OrderItem sellerItem = order.getItems().get(0);

            when(orderItemRepository.findBySellerId(sellerId)).thenReturn(List.of(sellerItem));

            // when
            PagedResponse<OrderResponse> response = sellerOrderService.getSellerOrders(sellerId, pageable);

            // then
            assertThat(response).isNotNull();
            assertThat(response.content()).hasSize(1);
            assertThat(response.totalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should return empty response when seller has no order items")
        void shouldReturnEmptyResponseWhenNoSellerItems() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            when(orderItemRepository.findBySellerId(sellerId)).thenReturn(List.of());

            // when
            PagedResponse<OrderResponse> response = sellerOrderService.getSellerOrders(sellerId, pageable);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should deduplicate orders when seller has multiple items in same order")
        void shouldDeduplicateOrdersWithMultipleItems() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Order order = createTestOrderWithSellerItem(sellerId);

            // Add a second item from the same seller to the same order
            OrderItem secondItem = OrderItem.builder()
                    .productId(UUID.randomUUID())
                    .sellerId(sellerId)
                    .productName("Second Product")
                    .unitPrice(BigDecimal.valueOf(50))
                    .quantity(2)
                    .totalPrice(BigDecimal.valueOf(100))
                    .status(OrderItemStatus.PENDING)
                    .build();
            secondItem.setId(UUID.randomUUID());
            order.addItem(secondItem);

            when(orderItemRepository.findBySellerId(sellerId)).thenReturn(List.of(order.getItems().get(0), secondItem));

            // when
            PagedResponse<OrderResponse> response = sellerOrderService.getSellerOrders(sellerId, pageable);

            // then
            assertThat(response.content()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("updateItemStatus")
    class UpdateItemStatusTests {

        @Test
        @DisplayName("Should update item status successfully when seller owns the item")
        void shouldUpdateItemStatusSuccessfully() {
            // given
            Order order = createTestOrderWithSellerItem(sellerId);
            UpdateItemStatusRequest request = new UpdateItemStatusRequest(OrderItemStatus.CONFIRMED);
            when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            OrderItemResponse response = sellerOrderService.updateItemStatus(sellerId, orderNumber, itemId, request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.status()).isEqualTo(OrderItemStatus.CONFIRMED);
            assertThat(response.id()).isEqualTo(itemId);
            verify(orderRepository).save(order);
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when seller does not own the item")
        void shouldThrowWhenSellerDoesNotOwnItem() {
            // given
            UUID differentSellerId = UUID.randomUUID();
            Order order = createTestOrderWithSellerItem(sellerId);
            UpdateItemStatusRequest request = new UpdateItemStatusRequest(OrderItemStatus.CONFIRMED);
            when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(order));

            // when/then
            assertThatThrownBy(() -> sellerOrderService.updateItemStatus(differentSellerId, orderNumber, itemId, request))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("permission");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when order not found")
        void shouldThrowWhenOrderNotFound() {
            // given
            UpdateItemStatusRequest request = new UpdateItemStatusRequest(OrderItemStatus.CONFIRMED);
            when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> sellerOrderService.updateItemStatus(sellerId, orderNumber, itemId, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Order");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when item not found in order")
        void shouldThrowWhenItemNotFoundInOrder() {
            // given
            Order order = createTestOrderWithSellerItem(sellerId);
            UUID nonExistentItemId = UUID.randomUUID();
            UpdateItemStatusRequest request = new UpdateItemStatusRequest(OrderItemStatus.CONFIRMED);
            when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(order));

            // when/then
            assertThatThrownBy(() -> sellerOrderService.updateItemStatus(sellerId, orderNumber, nonExistentItemId, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("OrderItem");
        }

        @Test
        @DisplayName("Should update item status to SHIPPED")
        void shouldUpdateItemStatusToShipped() {
            // given
            Order order = createTestOrderWithSellerItem(sellerId);
            order.getItems().get(0).setStatus(OrderItemStatus.CONFIRMED);
            UpdateItemStatusRequest request = new UpdateItemStatusRequest(OrderItemStatus.SHIPPED);
            when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            OrderItemResponse response = sellerOrderService.updateItemStatus(sellerId, orderNumber, itemId, request);

            // then
            assertThat(response.status()).isEqualTo(OrderItemStatus.SHIPPED);
        }
    }
}
