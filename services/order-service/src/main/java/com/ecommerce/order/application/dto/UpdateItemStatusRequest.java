package com.ecommerce.order.application.dto;

import com.ecommerce.order.domain.model.OrderItemStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateItemStatusRequest(
    @NotNull(message = "Status is required")
    OrderItemStatus status
) {}
