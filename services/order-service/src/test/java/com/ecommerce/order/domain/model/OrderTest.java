package com.ecommerce.order.domain.model;

import com.ecommerce.common.exception.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Order Domain Model Tests")
class OrderTest {

    private Order order;

    @BeforeEach
    void setUp() {
        order = Order.builder()
                .orderNumber("TY-20260301-TEST01")
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
                .totalAmount(BigDecimal.valueOf(200))
                .discountAmount(BigDecimal.ZERO)
                .shippingCost(BigDecimal.ZERO)
                .finalAmount(BigDecimal.valueOf(200))
                .build();
        order.setId(UUID.randomUUID());
    }

    @Nested
    @DisplayName("transitionTo")
    class TransitionToTests {

        @Test
        @DisplayName("Should transition from CREATED to PAYMENT_PENDING")
        void shouldTransitionFromCreatedToPaymentPending() {
            // when
            order.transitionTo(OrderStatus.PAYMENT_PENDING);

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_PENDING);
        }

        @Test
        @DisplayName("Should transition from CREATED to CANCELLED")
        void shouldTransitionFromCreatedToCancelled() {
            // when
            order.transitionTo(OrderStatus.CANCELLED);

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        @DisplayName("Should transition through full order lifecycle")
        void shouldTransitionThroughFullLifecycle() {
            // CREATED -> PAYMENT_PENDING
            order.transitionTo(OrderStatus.PAYMENT_PENDING);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_PENDING);

            // PAYMENT_PENDING -> PAYMENT_COMPLETED
            order.transitionTo(OrderStatus.PAYMENT_COMPLETED);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_COMPLETED);

            // PAYMENT_COMPLETED -> PROCESSING
            order.transitionTo(OrderStatus.PROCESSING);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PROCESSING);

            // PROCESSING -> SHIPPED
            order.transitionTo(OrderStatus.SHIPPED);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);

            // SHIPPED -> DELIVERED
            order.transitionTo(OrderStatus.DELIVERED);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @Test
        @DisplayName("Should transition through refund flow")
        void shouldTransitionThroughRefundFlow() {
            // Setup: Get to DELIVERED state
            order.transitionTo(OrderStatus.PAYMENT_PENDING);
            order.transitionTo(OrderStatus.PAYMENT_COMPLETED);
            order.transitionTo(OrderStatus.PROCESSING);
            order.transitionTo(OrderStatus.SHIPPED);
            order.transitionTo(OrderStatus.DELIVERED);

            // DELIVERED -> REFUND_REQUESTED
            order.transitionTo(OrderStatus.REFUND_REQUESTED);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.REFUND_REQUESTED);

            // REFUND_REQUESTED -> REFUNDED
            order.transitionTo(OrderStatus.REFUNDED);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.REFUNDED);
        }

        @Test
        @DisplayName("Should throw IllegalStateException for invalid transition CREATED to DELIVERED")
        void shouldThrowForInvalidTransitionCreatedToDelivered() {
            // when/then
            assertThatThrownBy(() -> order.transitionTo(OrderStatus.DELIVERED))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Cannot transition order from CREATED to DELIVERED");
        }

        @Test
        @DisplayName("Should throw BusinessRuleException for invalid transition CREATED to SHIPPED")
        void shouldThrowForInvalidTransitionCreatedToShipped() {
            // when/then
            assertThatThrownBy(() -> order.transitionTo(OrderStatus.SHIPPED))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Cannot transition order from CREATED to SHIPPED");
        }

        @Test
        @DisplayName("Should throw BusinessRuleException for transition from CANCELLED")
        void shouldThrowForTransitionFromCancelled() {
            // given
            order.transitionTo(OrderStatus.CANCELLED);

            // when/then
            assertThatThrownBy(() -> order.transitionTo(OrderStatus.CREATED))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Cannot transition order from CANCELLED to CREATED");
        }

        @Test
        @DisplayName("Should throw BusinessRuleException for transition from REFUNDED")
        void shouldThrowForTransitionFromRefunded() {
            // given
            order.transitionTo(OrderStatus.PAYMENT_PENDING);
            order.transitionTo(OrderStatus.PAYMENT_COMPLETED);
            order.transitionTo(OrderStatus.PROCESSING);
            order.transitionTo(OrderStatus.SHIPPED);
            order.transitionTo(OrderStatus.DELIVERED);
            order.transitionTo(OrderStatus.REFUND_REQUESTED);
            order.transitionTo(OrderStatus.REFUNDED);

            // when/then
            assertThatThrownBy(() -> order.transitionTo(OrderStatus.CREATED))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Cannot transition order from REFUNDED to CREATED");
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when SHIPPED order tries to cancel")
        void shouldThrowWhenShippedOrderTriesToCancel() {
            // given
            order.transitionTo(OrderStatus.PAYMENT_PENDING);
            order.transitionTo(OrderStatus.PAYMENT_COMPLETED);
            order.transitionTo(OrderStatus.PROCESSING);
            order.transitionTo(OrderStatus.SHIPPED);

            // when/then
            assertThatThrownBy(() -> order.transitionTo(OrderStatus.CANCELLED))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Cannot transition order from SHIPPED to CANCELLED");
        }
    }

    @Nested
    @DisplayName("addItem")
    class AddItemTests {

        @Test
        @DisplayName("Should add item to order and set order reference")
        void shouldAddItemToOrderAndSetReference() {
            // given
            OrderItem item = OrderItem.builder()
                    .productId(UUID.randomUUID())
                    .sellerId(UUID.randomUUID())
                    .productName("New Product")
                    .unitPrice(BigDecimal.valueOf(50))
                    .quantity(2)
                    .totalPrice(BigDecimal.valueOf(100))
                    .status(OrderItemStatus.PENDING)
                    .build();

            // when
            order.addItem(item);

            // then
            assertThat(order.getItems()).contains(item);
            assertThat(item.getOrder()).isEqualTo(order);
        }

        @Test
        @DisplayName("Should add multiple items to order")
        void shouldAddMultipleItemsToOrder() {
            // given
            OrderItem item1 = OrderItem.builder()
                    .productId(UUID.randomUUID())
                    .sellerId(UUID.randomUUID())
                    .productName("Product 1")
                    .unitPrice(BigDecimal.valueOf(50))
                    .quantity(1)
                    .totalPrice(BigDecimal.valueOf(50))
                    .status(OrderItemStatus.PENDING)
                    .build();

            OrderItem item2 = OrderItem.builder()
                    .productId(UUID.randomUUID())
                    .sellerId(UUID.randomUUID())
                    .productName("Product 2")
                    .unitPrice(BigDecimal.valueOf(75))
                    .quantity(1)
                    .totalPrice(BigDecimal.valueOf(75))
                    .status(OrderItemStatus.PENDING)
                    .build();

            // when
            order.addItem(item1);
            order.addItem(item2);

            // then
            assertThat(order.getItems()).hasSize(2);
            assertThat(order.getItems()).containsExactly(item1, item2);
        }
    }
}
