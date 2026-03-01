package com.trendyolclone.order.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OrderStatus State Machine Tests")
class OrderStatusTest {

    @Nested
    @DisplayName("CREATED transitions")
    class CreatedTransitions {

        @Test
        @DisplayName("CREATED can transition to PAYMENT_PENDING")
        void canTransitionToPaymentPending() {
            assertThat(OrderStatus.CREATED.canTransitionTo(OrderStatus.PAYMENT_PENDING)).isTrue();
        }

        @Test
        @DisplayName("CREATED can transition to CANCELLED")
        void canTransitionToCancelled() {
            assertThat(OrderStatus.CREATED.canTransitionTo(OrderStatus.CANCELLED)).isTrue();
        }

        @Test
        @DisplayName("CREATED cannot transition to DELIVERED")
        void cannotTransitionToDelivered() {
            assertThat(OrderStatus.CREATED.canTransitionTo(OrderStatus.DELIVERED)).isFalse();
        }

        @Test
        @DisplayName("CREATED cannot transition to SHIPPED")
        void cannotTransitionToShipped() {
            assertThat(OrderStatus.CREATED.canTransitionTo(OrderStatus.SHIPPED)).isFalse();
        }

        @Test
        @DisplayName("CREATED cannot transition to PROCESSING")
        void cannotTransitionToProcessing() {
            assertThat(OrderStatus.CREATED.canTransitionTo(OrderStatus.PROCESSING)).isFalse();
        }

        @Test
        @DisplayName("CREATED cannot transition to REFUNDED")
        void cannotTransitionToRefunded() {
            assertThat(OrderStatus.CREATED.canTransitionTo(OrderStatus.REFUNDED)).isFalse();
        }
    }

    @Nested
    @DisplayName("PAYMENT_PENDING transitions")
    class PaymentPendingTransitions {

        @Test
        @DisplayName("PAYMENT_PENDING can transition to PAYMENT_COMPLETED")
        void canTransitionToPaymentCompleted() {
            assertThat(OrderStatus.PAYMENT_PENDING.canTransitionTo(OrderStatus.PAYMENT_COMPLETED)).isTrue();
        }

        @Test
        @DisplayName("PAYMENT_PENDING can transition to CANCELLED")
        void canTransitionToCancelled() {
            assertThat(OrderStatus.PAYMENT_PENDING.canTransitionTo(OrderStatus.CANCELLED)).isTrue();
        }

        @Test
        @DisplayName("PAYMENT_PENDING cannot transition to DELIVERED")
        void cannotTransitionToDelivered() {
            assertThat(OrderStatus.PAYMENT_PENDING.canTransitionTo(OrderStatus.DELIVERED)).isFalse();
        }
    }

    @Nested
    @DisplayName("PAYMENT_COMPLETED transitions")
    class PaymentCompletedTransitions {

        @Test
        @DisplayName("PAYMENT_COMPLETED can transition to PROCESSING")
        void canTransitionToProcessing() {
            assertThat(OrderStatus.PAYMENT_COMPLETED.canTransitionTo(OrderStatus.PROCESSING)).isTrue();
        }

        @Test
        @DisplayName("PAYMENT_COMPLETED can transition to CANCELLED")
        void canTransitionToCancelled() {
            assertThat(OrderStatus.PAYMENT_COMPLETED.canTransitionTo(OrderStatus.CANCELLED)).isTrue();
        }

        @Test
        @DisplayName("PAYMENT_COMPLETED cannot transition to SHIPPED directly")
        void cannotTransitionToShippedDirectly() {
            assertThat(OrderStatus.PAYMENT_COMPLETED.canTransitionTo(OrderStatus.SHIPPED)).isFalse();
        }
    }

    @Nested
    @DisplayName("PROCESSING transitions")
    class ProcessingTransitions {

        @Test
        @DisplayName("PROCESSING can transition to SHIPPED")
        void canTransitionToShipped() {
            assertThat(OrderStatus.PROCESSING.canTransitionTo(OrderStatus.SHIPPED)).isTrue();
        }

        @Test
        @DisplayName("PROCESSING can transition to CANCELLED")
        void canTransitionToCancelled() {
            assertThat(OrderStatus.PROCESSING.canTransitionTo(OrderStatus.CANCELLED)).isTrue();
        }

        @Test
        @DisplayName("PROCESSING cannot transition to DELIVERED directly")
        void cannotTransitionToDeliveredDirectly() {
            assertThat(OrderStatus.PROCESSING.canTransitionTo(OrderStatus.DELIVERED)).isFalse();
        }
    }

    @Nested
    @DisplayName("SHIPPED transitions")
    class ShippedTransitions {

        @Test
        @DisplayName("SHIPPED can transition to DELIVERED")
        void canTransitionToDelivered() {
            assertThat(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.DELIVERED)).isTrue();
        }

        @Test
        @DisplayName("SHIPPED cannot transition to CANCELLED")
        void cannotTransitionToCancelled() {
            assertThat(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.CANCELLED)).isFalse();
        }

        @Test
        @DisplayName("SHIPPED cannot transition to PROCESSING")
        void cannotTransitionToProcessing() {
            assertThat(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.PROCESSING)).isFalse();
        }
    }

    @Nested
    @DisplayName("DELIVERED transitions")
    class DeliveredTransitions {

        @Test
        @DisplayName("DELIVERED can transition to REFUND_REQUESTED")
        void canTransitionToRefundRequested() {
            assertThat(OrderStatus.DELIVERED.canTransitionTo(OrderStatus.REFUND_REQUESTED)).isTrue();
        }

        @Test
        @DisplayName("DELIVERED cannot transition to CANCELLED")
        void cannotTransitionToCancelled() {
            assertThat(OrderStatus.DELIVERED.canTransitionTo(OrderStatus.CANCELLED)).isFalse();
        }

        @Test
        @DisplayName("DELIVERED cannot transition to SHIPPED")
        void cannotTransitionToShipped() {
            assertThat(OrderStatus.DELIVERED.canTransitionTo(OrderStatus.SHIPPED)).isFalse();
        }
    }

    @Nested
    @DisplayName("Terminal state transitions")
    class TerminalStateTransitions {

        @ParameterizedTest
        @EnumSource(OrderStatus.class)
        @DisplayName("CANCELLED cannot transition to any state")
        void cancelledCannotTransitionToAnyState(OrderStatus target) {
            assertThat(OrderStatus.CANCELLED.canTransitionTo(target)).isFalse();
        }

        @ParameterizedTest
        @EnumSource(OrderStatus.class)
        @DisplayName("REFUNDED cannot transition to any state")
        void refundedCannotTransitionToAnyState(OrderStatus target) {
            assertThat(OrderStatus.REFUNDED.canTransitionTo(target)).isFalse();
        }
    }

    @Nested
    @DisplayName("REFUND_REQUESTED transitions")
    class RefundRequestedTransitions {

        @Test
        @DisplayName("REFUND_REQUESTED can transition to REFUNDED")
        void canTransitionToRefunded() {
            assertThat(OrderStatus.REFUND_REQUESTED.canTransitionTo(OrderStatus.REFUNDED)).isTrue();
        }

        @Test
        @DisplayName("REFUND_REQUESTED cannot transition to CANCELLED")
        void cannotTransitionToCancelled() {
            assertThat(OrderStatus.REFUND_REQUESTED.canTransitionTo(OrderStatus.CANCELLED)).isFalse();
        }
    }
}
