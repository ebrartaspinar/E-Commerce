package com.ecommerce.order.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecommerce.common.dto.PagedResponse;
import com.ecommerce.order.application.dto.*;
import com.ecommerce.order.application.service.OrderService;
import com.ecommerce.order.domain.model.OrderItemStatus;
import com.ecommerce.order.domain.model.OrderStatus;
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
import com.ecommerce.order.infrastructure.config.SecurityConfig;
import com.ecommerce.order.infrastructure.config.JwtAuthenticationFilter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
        value = OrderController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("OrderController Integration Tests")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private UUID userId;
    private String orderNumber;
    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        orderNumber = "TY-20260301-ABC123";

        // Set up security context
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        OrderItemResponse itemResponse = new OrderItemResponse(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Test Product",
                null,
                BigDecimal.valueOf(100),
                1,
                BigDecimal.valueOf(100),
                OrderItemStatus.PENDING
        );

        OrderAddressResponse addressResponse = new OrderAddressResponse(
                "Test User",
                "5551234567",
                "Test Address",
                "Istanbul",
                "Kadikoy",
                "34000"
        );

        orderResponse = new OrderResponse(
                UUID.randomUUID(),
                orderNumber,
                userId,
                OrderStatus.CREATED,
                List.of(itemResponse),
                addressResponse,
                BigDecimal.valueOf(100),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.valueOf(100),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("POST /api/v1/orders should create order and return 201")
    void createOrderShouldReturn201() throws Exception {
        // given
        CreateOrderRequest request = new CreateOrderRequest(UUID.randomUUID(), "Test order notes");
        when(orderService.createOrder(eq(userId), any(CreateOrderRequest.class))).thenReturn(orderResponse);

        // when/then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNumber").value(orderNumber))
                .andExpect(jsonPath("$.data.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Order created successfully"));
    }

    @Test
    @DisplayName("GET /api/v1/orders should return paged orders and 200")
    void getOrdersShouldReturn200() throws Exception {
        // given
        PagedResponse<OrderResponse> pagedResponse = new PagedResponse<>(
                List.of(orderResponse), 0, 10, 1, 1, true, true
        );
        when(orderService.getOrders(eq(userId), any(Pageable.class))).thenReturn(pagedResponse);

        // when/then
        mockMvc.perform(get("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].orderNumber").value(orderNumber))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/orders/{orderNumber} should return order and 200")
    void getOrderByNumberShouldReturn200() throws Exception {
        // given
        when(orderService.getOrder(userId, orderNumber)).thenReturn(orderResponse);

        // when/then
        mockMvc.perform(get("/api/v1/orders/{orderNumber}", orderNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNumber").value(orderNumber))
                .andExpect(jsonPath("$.data.status").value("CREATED"))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[0].productName").value("Test Product"));
    }

    @Test
    @DisplayName("PATCH /api/v1/orders/{orderNumber}/cancel should cancel order and return 200")
    void cancelOrderShouldReturn200() throws Exception {
        // given
        CancelOrderRequest cancelRequest = new CancelOrderRequest("Changed my mind");
        OrderResponse cancelledResponse = new OrderResponse(
                orderResponse.id(),
                orderNumber,
                userId,
                OrderStatus.CANCELLED,
                orderResponse.items(),
                orderResponse.shippingAddress(),
                orderResponse.totalAmount(),
                orderResponse.discountAmount(),
                orderResponse.shippingCost(),
                orderResponse.finalAmount(),
                orderResponse.createdAt()
        );
        when(orderService.cancelOrder(eq(userId), eq(orderNumber), any(CancelOrderRequest.class)))
                .thenReturn(cancelledResponse);

        // when/then
        mockMvc.perform(patch("/api/v1/orders/{orderNumber}/cancel", orderNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CANCELLED"))
                .andExpect(jsonPath("$.message").value("Order cancelled successfully"));
    }
}
