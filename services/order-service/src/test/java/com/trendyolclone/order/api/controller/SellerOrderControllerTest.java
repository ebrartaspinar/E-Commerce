package com.trendyolclone.order.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trendyolclone.common.dto.PagedResponse;
import com.trendyolclone.order.application.dto.*;
import com.trendyolclone.order.application.service.SellerOrderService;
import com.trendyolclone.order.domain.model.OrderItemStatus;
import com.trendyolclone.order.domain.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import com.trendyolclone.order.infrastructure.config.SecurityConfig;
import com.trendyolclone.order.infrastructure.config.JwtAuthenticationFilter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = SellerOrderController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("SellerOrderController Integration Tests")
class SellerOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SellerOrderService sellerOrderService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private UUID sellerId;
    private UUID itemId;
    private String orderNumber;

    @BeforeEach
    void setUp() {
        sellerId = UUID.randomUUID();
        itemId = UUID.randomUUID();
        orderNumber = "TY-20260301-ABC123";

        // Set up security context with SELLER role
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        sellerId, null,
                        List.of(new SimpleGrantedAuthority("ROLE_SELLER"))
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("GET /api/v1/seller/orders should return seller orders and 200")
    void getSellerOrdersShouldReturn200() throws Exception {
        // given
        OrderItemResponse itemResponse = new OrderItemResponse(
                itemId, UUID.randomUUID(), sellerId,
                "Seller Product", null,
                BigDecimal.valueOf(100), 1, BigDecimal.valueOf(100),
                OrderItemStatus.PENDING
        );

        OrderAddressResponse addressResponse = new OrderAddressResponse(
                "Buyer User", "5551234567", "Buyer Address",
                "Istanbul", "Kadikoy", "34000"
        );

        OrderResponse orderResponse = new OrderResponse(
                UUID.randomUUID(), orderNumber, UUID.randomUUID(),
                OrderStatus.CREATED, List.of(itemResponse), addressResponse,
                BigDecimal.valueOf(100), BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.valueOf(100), LocalDateTime.now()
        );

        PagedResponse<OrderResponse> pagedResponse = new PagedResponse<>(
                List.of(orderResponse), 0, 10, 1, 1, true, true
        );
        when(sellerOrderService.getSellerOrders(eq(sellerId), any(Pageable.class))).thenReturn(pagedResponse);

        // when/then
        mockMvc.perform(get("/api/v1/seller/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].orderNumber").value(orderNumber))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("PATCH /api/v1/seller/orders/{orderNumber}/items/{itemId}/status should update item status and return 200")
    void updateItemStatusShouldReturn200() throws Exception {
        // given
        UpdateItemStatusRequest request = new UpdateItemStatusRequest(OrderItemStatus.CONFIRMED);

        OrderItemResponse updatedItemResponse = new OrderItemResponse(
                itemId, UUID.randomUUID(), sellerId,
                "Seller Product", null,
                BigDecimal.valueOf(100), 1, BigDecimal.valueOf(100),
                OrderItemStatus.CONFIRMED
        );

        when(sellerOrderService.updateItemStatus(
                eq(sellerId), eq(orderNumber), eq(itemId), any(UpdateItemStatusRequest.class)))
                .thenReturn(updatedItemResponse);

        // when/then
        mockMvc.perform(patch("/api/v1/seller/orders/{orderNumber}/items/{itemId}/status", orderNumber, itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.data.id").value(itemId.toString()))
                .andExpect(jsonPath("$.message").value("Order item status updated"));
    }
}
