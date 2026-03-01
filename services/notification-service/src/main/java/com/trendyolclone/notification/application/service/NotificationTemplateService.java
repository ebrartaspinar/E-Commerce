package com.trendyolclone.notification.application.service;

import com.trendyolclone.notification.domain.model.NotificationChannel;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationTemplateService {

    public String renderTemplate(NotificationChannel channel, Map<String, String> params) {
        return switch (channel) {
            case WELCOME -> String.format(
                    "Welcome to TrendyolClone, %s! Start exploring amazing deals.",
                    params.getOrDefault("name", "User")
            );
            case ORDER_CONFIRMATION -> String.format(
                    "Your order %s has been placed successfully!",
                    params.getOrDefault("orderNumber", "")
            );
            case PAYMENT_SUCCESS -> String.format(
                    "Payment of %s TRY for order %s was successful.",
                    params.getOrDefault("amount", "0"),
                    params.getOrDefault("orderNumber", "")
            );
            case PAYMENT_FAILED -> String.format(
                    "Payment for order %s has failed. Please try again.",
                    params.getOrDefault("orderNumber", "")
            );
            case ORDER_SHIPPED -> String.format(
                    "Your order %s has been shipped!",
                    params.getOrDefault("orderNumber", "")
            );
            case ORDER_DELIVERED -> String.format(
                    "Your order %s has been delivered. Enjoy!",
                    params.getOrDefault("orderNumber", "")
            );
            case STOCK_LOW -> String.format(
                    "Low stock alert: Product '%s' has only %s items left.",
                    params.getOrDefault("productName", ""),
                    params.getOrDefault("stockCount", "0")
            );
            case PRICE_DROP -> String.format(
                    "Price drop! '%s' is now available at a lower price.",
                    params.getOrDefault("productName", "")
            );
        };
    }

    public String getTitleForChannel(NotificationChannel channel) {
        return switch (channel) {
            case WELCOME -> "Welcome to TrendyolClone!";
            case ORDER_CONFIRMATION -> "Order Confirmed";
            case PAYMENT_SUCCESS -> "Payment Successful";
            case PAYMENT_FAILED -> "Payment Failed";
            case ORDER_SHIPPED -> "Order Shipped";
            case ORDER_DELIVERED -> "Order Delivered";
            case STOCK_LOW -> "Low Stock Alert";
            case PRICE_DROP -> "Price Drop Alert";
        };
    }
}
