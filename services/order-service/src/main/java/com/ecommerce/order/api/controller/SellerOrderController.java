package com.ecommerce.order.api.controller;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.common.dto.PagedResponse;
import com.ecommerce.order.application.dto.OrderItemResponse;
import com.ecommerce.order.application.dto.OrderResponse;
import com.ecommerce.order.application.dto.UpdateItemStatusRequest;
import com.ecommerce.order.application.service.SellerOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seller/orders")
@PreAuthorize("hasRole('SELLER')")
@RequiredArgsConstructor
@Tag(name = "Seller Orders", description = "Seller order management endpoints")
public class SellerOrderController {

    private final SellerOrderService sellerOrderService;

    @GetMapping
    @Operation(summary = "Get all orders for the authenticated seller")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> getSellerOrders(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        UUID sellerId = getAuthenticatedUserId();
        PagedResponse<OrderResponse> response = sellerOrderService.getSellerOrders(sellerId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{orderNumber}/items/{itemId}/status")
    @Operation(summary = "Update the status of an order item")
    public ResponseEntity<ApiResponse<OrderItemResponse>> updateItemStatus(
            @PathVariable String orderNumber,
            @PathVariable UUID itemId,
            @Valid @RequestBody UpdateItemStatusRequest request) {
        UUID sellerId = getAuthenticatedUserId();
        OrderItemResponse response = sellerOrderService.updateItemStatus(sellerId, orderNumber, itemId, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Order item status updated"));
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UUID) authentication.getPrincipal();
    }
}
