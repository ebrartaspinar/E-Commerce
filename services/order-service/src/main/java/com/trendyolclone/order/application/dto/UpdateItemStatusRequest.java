package com.trendyolclone.order.application.dto;

import com.trendyolclone.order.domain.model.OrderItemStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateItemStatusRequest(
    @NotNull(message = "Status is required")
    OrderItemStatus status
) {}
