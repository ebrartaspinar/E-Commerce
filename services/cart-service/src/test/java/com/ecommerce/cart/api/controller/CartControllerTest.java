package com.ecommerce.cart.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecommerce.cart.application.dto.*;
import com.ecommerce.cart.application.service.CartService;
import com.ecommerce.common.exception.BusinessRuleException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import com.ecommerce.cart.infrastructure.config.SecurityConfig;
import com.ecommerce.cart.infrastructure.config.JwtAuthenticationFilter;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = CartController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CartController Integration Tests")
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService cartService;

    private UUID userId;
    private String userIdStr;
    private CartResponse sampleCartResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userIdStr = userId.toString();

        CartItemResponse itemResponse = new CartItemResponse(
                "product-1", "Test Product", new BigDecimal("29.99"), 2,
                "http://img.jpg", "seller-1", LocalDateTime.now(),
                false, new BigDecimal("29.99"), true
        );

        sampleCartResponse = new CartResponse(
                userIdStr,
                List.of(itemResponse),
                new BigDecimal("59.98"),
                2
        );

        // Set up security context - CartController casts principal to UUID
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Nested
    @DisplayName("GET /api/v1/cart")
    class GetCart {

        @Test
        @DisplayName("Should return 200 with cart contents")
        void shouldReturn200WithCartContents() throws Exception {
            // given
            when(cartService.getCart(userIdStr)).thenReturn(sampleCartResponse);

            // when / then
            mockMvc.perform(get("/api/v1/cart")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(userIdStr))
                    .andExpect(jsonPath("$.data.items").isArray())
                    .andExpect(jsonPath("$.data.items[0].productId").value("product-1"))
                    .andExpect(jsonPath("$.data.items[0].productName").value("Test Product"))
                    .andExpect(jsonPath("$.data.items[0].quantity").value(2))
                    .andExpect(jsonPath("$.data.totalAmount").value(59.98))
                    .andExpect(jsonPath("$.data.itemCount").value(2));
        }

        @Test
        @DisplayName("Should return 200 with empty cart when user has no items")
        void shouldReturn200WithEmptyCart() throws Exception {
            // given
            CartResponse emptyCart = new CartResponse(
                    userIdStr, Collections.emptyList(), BigDecimal.ZERO, 0
            );
            when(cartService.getCart(userIdStr)).thenReturn(emptyCart);

            // when / then
            mockMvc.perform(get("/api/v1/cart")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.items").isEmpty())
                    .andExpect(jsonPath("$.data.itemCount").value(0));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/cart/items")
    class AddItem {

        @Test
        @DisplayName("Should return 201 when item is added successfully")
        void shouldReturn201WhenItemAddedSuccessfully() throws Exception {
            // given
            AddToCartRequest request = new AddToCartRequest("product-2", 1);

            when(cartService.addItem(eq(userIdStr), any(AddToCartRequest.class)))
                    .thenReturn(sampleCartResponse);

            // when / then
            mockMvc.perform(post("/api/v1/cart/items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(userIdStr))
                    .andExpect(jsonPath("$.message").value("Item added to cart"));
        }

        @Test
        @DisplayName("Should return 422 when cart exceeds max items limit")
        void shouldReturn422WhenCartExceedsMaxLimit() throws Exception {
            // given
            AddToCartRequest request = new AddToCartRequest("product-new", 1);

            when(cartService.addItem(eq(userIdStr), any(AddToCartRequest.class)))
                    .thenThrow(new BusinessRuleException("Cart cannot contain more than 50 items"));

            // when / then
            mockMvc.perform(post("/api/v1/cart/items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("Should return 422 when single product quantity exceeds 10")
        void shouldReturn422WhenSingleProductQuantityExceeds10() throws Exception {
            // given
            AddToCartRequest request = new AddToCartRequest("product-1", 5);

            when(cartService.addItem(eq(userIdStr), any(AddToCartRequest.class)))
                    .thenThrow(new BusinessRuleException("Quantity for a single product cannot exceed 10"));

            // when / then
            mockMvc.perform(post("/api/v1/cart/items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/cart/items/{productId}")
    class UpdateQuantity {

        @Test
        @DisplayName("Should return 200 when quantity is updated successfully")
        void shouldReturn200WhenQuantityUpdatedSuccessfully() throws Exception {
            // given
            UpdateQuantityRequest request = new UpdateQuantityRequest(5);

            when(cartService.updateQuantity(eq(userIdStr), eq("product-1"), any(UpdateQuantityRequest.class)))
                    .thenReturn(sampleCartResponse);

            // when / then
            mockMvc.perform(put("/api/v1/cart/items/{productId}", "product-1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Quantity updated"));
        }

        @Test
        @DisplayName("Should return 404 when product not found in cart")
        void shouldReturn404WhenProductNotFoundInCart() throws Exception {
            // given
            UpdateQuantityRequest request = new UpdateQuantityRequest(3);

            when(cartService.updateQuantity(eq(userIdStr), eq("nonexistent"), any(UpdateQuantityRequest.class)))
                    .thenThrow(new ResourceNotFoundException("CartItem", "productId", "nonexistent"));

            // when / then
            mockMvc.perform(put("/api/v1/cart/items/{productId}", "nonexistent")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/cart/items/{productId}")
    class RemoveItem {

        @Test
        @DisplayName("Should return 200 when item is removed successfully")
        void shouldReturn200WhenItemRemovedSuccessfully() throws Exception {
            // given
            CartResponse updatedCart = new CartResponse(
                    userIdStr, Collections.emptyList(), BigDecimal.ZERO, 0
            );

            when(cartService.removeItem(userIdStr, "product-1")).thenReturn(updatedCart);

            // when / then
            mockMvc.perform(delete("/api/v1/cart/items/{productId}", "product-1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Item removed from cart"))
                    .andExpect(jsonPath("$.data.items").isEmpty());
        }

        @Test
        @DisplayName("Should return 404 when removing nonexistent item")
        void shouldReturn404WhenRemovingNonexistentItem() throws Exception {
            // given
            when(cartService.removeItem(userIdStr, "nonexistent"))
                    .thenThrow(new ResourceNotFoundException("CartItem", "productId", "nonexistent"));

            // when / then
            mockMvc.perform(delete("/api/v1/cart/items/{productId}", "nonexistent")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/cart")
    class ClearCart {

        @Test
        @DisplayName("Should return 200 when cart is cleared successfully")
        void shouldReturn200WhenCartClearedSuccessfully() throws Exception {
            // given
            doNothing().when(cartService).clearCart(userIdStr);

            // when / then
            mockMvc.perform(delete("/api/v1/cart")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Cart cleared"));

            verify(cartService).clearCart(userIdStr);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/cart/summary")
    class GetSummary {

        @Test
        @DisplayName("Should return 200 with cart summary")
        void shouldReturn200WithCartSummary() throws Exception {
            // given
            CartSummaryResponse summary = new CartSummaryResponse(
                    new BigDecimal("59.98"), 2, "2-4 business days"
            );

            when(cartService.getSummary(userIdStr)).thenReturn(summary);

            // when / then
            mockMvc.perform(get("/api/v1/cart/summary")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.totalAmount").value(59.98))
                    .andExpect(jsonPath("$.data.itemCount").value(2))
                    .andExpect(jsonPath("$.data.estimatedDelivery").value("2-4 business days"));
        }

        @Test
        @DisplayName("Should return 200 with zero summary for empty cart")
        void shouldReturn200WithZeroSummaryForEmptyCart() throws Exception {
            // given
            CartSummaryResponse summary = new CartSummaryResponse(
                    BigDecimal.ZERO, 0, "2-4 business days"
            );

            when(cartService.getSummary(userIdStr)).thenReturn(summary);

            // when / then
            mockMvc.perform(get("/api/v1/cart/summary")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalAmount").value(0))
                    .andExpect(jsonPath("$.data.itemCount").value(0));
        }
    }
}
