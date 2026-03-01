package com.trendyolclone.notification.application.service;

import com.trendyolclone.notification.domain.model.NotificationChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("NotificationTemplateService Unit Tests")
class NotificationTemplateServiceTest {

    private NotificationTemplateService templateService;

    @BeforeEach
    void setUp() {
        templateService = new NotificationTemplateService();
    }

    @Nested
    @DisplayName("renderTemplate")
    class RenderTemplateTests {

        @Test
        @DisplayName("Should render WELCOME template with user name")
        void shouldRenderWelcomeTemplate() {
            // given
            Map<String, String> params = Map.of("name", "John Doe");

            // when
            String result = templateService.renderTemplate(NotificationChannel.WELCOME, params);

            // then
            assertThat(result).isEqualTo("Welcome to TrendyolClone, John Doe! Start exploring amazing deals.");
        }

        @Test
        @DisplayName("Should render WELCOME template with default name when not provided")
        void shouldRenderWelcomeTemplateWithDefaultName() {
            // given
            Map<String, String> params = Map.of();

            // when
            String result = templateService.renderTemplate(NotificationChannel.WELCOME, params);

            // then
            assertThat(result).isEqualTo("Welcome to TrendyolClone, User! Start exploring amazing deals.");
        }

        @Test
        @DisplayName("Should render ORDER_CONFIRMATION template with order number")
        void shouldRenderOrderConfirmationTemplate() {
            // given
            Map<String, String> params = Map.of("orderNumber", "TY-20260301-ABC123");

            // when
            String result = templateService.renderTemplate(NotificationChannel.ORDER_CONFIRMATION, params);

            // then
            assertThat(result).isEqualTo("Your order TY-20260301-ABC123 has been placed successfully!");
        }

        @Test
        @DisplayName("Should render PAYMENT_SUCCESS template with amount and order number")
        void shouldRenderPaymentSuccessTemplate() {
            // given
            Map<String, String> params = Map.of(
                    "amount", "150.00",
                    "orderNumber", "TY-20260301-XYZ789"
            );

            // when
            String result = templateService.renderTemplate(NotificationChannel.PAYMENT_SUCCESS, params);

            // then
            assertThat(result).isEqualTo("Payment of 150.00 TRY for order TY-20260301-XYZ789 was successful.");
        }

        @Test
        @DisplayName("Should render PAYMENT_FAILED template with order number")
        void shouldRenderPaymentFailedTemplate() {
            // given
            Map<String, String> params = Map.of("orderNumber", "TY-20260301-FAIL01");

            // when
            String result = templateService.renderTemplate(NotificationChannel.PAYMENT_FAILED, params);

            // then
            assertThat(result).isEqualTo("Payment for order TY-20260301-FAIL01 has failed. Please try again.");
        }

        @Test
        @DisplayName("Should render ORDER_SHIPPED template with order number")
        void shouldRenderOrderShippedTemplate() {
            // given
            Map<String, String> params = Map.of("orderNumber", "TY-20260301-SHIP01");

            // when
            String result = templateService.renderTemplate(NotificationChannel.ORDER_SHIPPED, params);

            // then
            assertThat(result).isEqualTo("Your order TY-20260301-SHIP01 has been shipped!");
        }

        @Test
        @DisplayName("Should render ORDER_DELIVERED template with order number")
        void shouldRenderOrderDeliveredTemplate() {
            // given
            Map<String, String> params = Map.of("orderNumber", "TY-20260301-DLVR01");

            // when
            String result = templateService.renderTemplate(NotificationChannel.ORDER_DELIVERED, params);

            // then
            assertThat(result).isEqualTo("Your order TY-20260301-DLVR01 has been delivered. Enjoy!");
        }

        @Test
        @DisplayName("Should render STOCK_LOW template with product name and stock count")
        void shouldRenderStockLowTemplate() {
            // given
            Map<String, String> params = Map.of(
                    "productName", "Wireless Mouse",
                    "stockCount", "3"
            );

            // when
            String result = templateService.renderTemplate(NotificationChannel.STOCK_LOW, params);

            // then
            assertThat(result).isEqualTo("Low stock alert: Product 'Wireless Mouse' has only 3 items left.");
        }

        @Test
        @DisplayName("Should render PRICE_DROP template with product name")
        void shouldRenderPriceDropTemplate() {
            // given
            Map<String, String> params = Map.of("productName", "Bluetooth Headphones");

            // when
            String result = templateService.renderTemplate(NotificationChannel.PRICE_DROP, params);

            // then
            assertThat(result).isEqualTo("Price drop! 'Bluetooth Headphones' is now available at a lower price.");
        }

        @Test
        @DisplayName("Should use default values when params are missing for STOCK_LOW")
        void shouldUseDefaultValuesForStockLow() {
            // given
            Map<String, String> params = Map.of();

            // when
            String result = templateService.renderTemplate(NotificationChannel.STOCK_LOW, params);

            // then
            assertThat(result).isEqualTo("Low stock alert: Product '' has only 0 items left.");
        }

        @Test
        @DisplayName("Should use default values when params are missing for PAYMENT_SUCCESS")
        void shouldUseDefaultValuesForPaymentSuccess() {
            // given
            Map<String, String> params = Map.of();

            // when
            String result = templateService.renderTemplate(NotificationChannel.PAYMENT_SUCCESS, params);

            // then
            assertThat(result).isEqualTo("Payment of 0 TRY for order  was successful.");
        }
    }

    @Nested
    @DisplayName("getTitleForChannel")
    class GetTitleForChannelTests {

        @Test
        @DisplayName("Should return correct title for WELCOME channel")
        void shouldReturnWelcomeTitle() {
            assertThat(templateService.getTitleForChannel(NotificationChannel.WELCOME))
                    .isEqualTo("Welcome to TrendyolClone!");
        }

        @Test
        @DisplayName("Should return correct title for ORDER_CONFIRMATION channel")
        void shouldReturnOrderConfirmationTitle() {
            assertThat(templateService.getTitleForChannel(NotificationChannel.ORDER_CONFIRMATION))
                    .isEqualTo("Order Confirmed");
        }

        @Test
        @DisplayName("Should return correct title for PAYMENT_SUCCESS channel")
        void shouldReturnPaymentSuccessTitle() {
            assertThat(templateService.getTitleForChannel(NotificationChannel.PAYMENT_SUCCESS))
                    .isEqualTo("Payment Successful");
        }

        @Test
        @DisplayName("Should return correct title for PAYMENT_FAILED channel")
        void shouldReturnPaymentFailedTitle() {
            assertThat(templateService.getTitleForChannel(NotificationChannel.PAYMENT_FAILED))
                    .isEqualTo("Payment Failed");
        }

        @Test
        @DisplayName("Should return correct title for ORDER_SHIPPED channel")
        void shouldReturnOrderShippedTitle() {
            assertThat(templateService.getTitleForChannel(NotificationChannel.ORDER_SHIPPED))
                    .isEqualTo("Order Shipped");
        }

        @Test
        @DisplayName("Should return correct title for ORDER_DELIVERED channel")
        void shouldReturnOrderDeliveredTitle() {
            assertThat(templateService.getTitleForChannel(NotificationChannel.ORDER_DELIVERED))
                    .isEqualTo("Order Delivered");
        }

        @Test
        @DisplayName("Should return correct title for STOCK_LOW channel")
        void shouldReturnStockLowTitle() {
            assertThat(templateService.getTitleForChannel(NotificationChannel.STOCK_LOW))
                    .isEqualTo("Low Stock Alert");
        }

        @Test
        @DisplayName("Should return correct title for PRICE_DROP channel")
        void shouldReturnPriceDropTitle() {
            assertThat(templateService.getTitleForChannel(NotificationChannel.PRICE_DROP))
                    .isEqualTo("Price Drop Alert");
        }

        @ParameterizedTest
        @EnumSource(NotificationChannel.class)
        @DisplayName("Should return non-null title for every channel")
        void shouldReturnNonNullTitleForEveryChannel(NotificationChannel channel) {
            String title = templateService.getTitleForChannel(channel);
            assertThat(title).isNotNull().isNotBlank();
        }
    }
}
