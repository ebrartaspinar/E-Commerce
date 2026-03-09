package com.ecommerce.order.api.controller;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.common.dto.PagedResponse;
import com.ecommerce.order.application.dto.CancelOrderRequest;
import com.ecommerce.order.application.dto.CreateOrderRequest;
import com.ecommerce.order.application.dto.OrderResponse;
import com.ecommerce.order.application.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create a new order")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        UUID userId = getAuthenticatedUserId();
        OrderResponse response = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Order created successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all orders for current user")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> getOrders(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        UUID userId = getAuthenticatedUserId();
        PagedResponse<OrderResponse> response = orderService.getOrders(userId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{orderNumber}")
    @Operation(summary = "Get order by order number")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable String orderNumber) {
        UUID userId = getAuthenticatedUserId();
        OrderResponse response = orderService.getOrder(userId, orderNumber);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{orderNumber}/cancel")
    @Operation(summary = "Cancel an order")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable String orderNumber,
            @RequestBody CancelOrderRequest request) {
        UUID userId = getAuthenticatedUserId();
        OrderResponse response = orderService.cancelOrder(userId, orderNumber, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Order cancelled successfully"));
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UUID) authentication.getPrincipal();
    }
}
