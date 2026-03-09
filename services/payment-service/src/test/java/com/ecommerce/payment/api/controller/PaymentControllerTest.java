package com.ecommerce.payment.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecommerce.payment.application.dto.InitiatePaymentRequest;
import com.ecommerce.payment.application.dto.PaymentResponse;
import com.ecommerce.payment.application.service.PaymentService;
import com.ecommerce.payment.domain.model.PaymentMethod;
import com.ecommerce.payment.domain.model.PaymentStatus;
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
import com.ecommerce.payment.infrastructure.config.SecurityConfig;
import com.ecommerce.payment.infrastructure.config.JwtAuthenticationFilter;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = PaymentController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("PaymentController Integration Tests")
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private UUID paymentId;
    private UUID userId;
    private String orderNumber;
    private PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        paymentId = UUID.randomUUID();
        userId = UUID.randomUUID();
        orderNumber = "TY-20260301-ABC123";

        paymentResponse = new PaymentResponse(
                paymentId,
                orderNumber,
                userId,
                BigDecimal.valueOf(150),
                "TRY",
                PaymentMethod.CREDIT_CARD,
                PaymentStatus.COMPLETED,
                "TXN-abc12345",
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("POST /api/v1/payments should initiate payment and return 201")
    void initiatePaymentShouldReturn201() throws Exception {
        // given
        InitiatePaymentRequest request = new InitiatePaymentRequest(
                orderNumber, userId, BigDecimal.valueOf(150), PaymentMethod.CREDIT_CARD
        );
        when(paymentService.initiatePayment(any(InitiatePaymentRequest.class))).thenReturn(paymentResponse);

        // when/then
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(paymentId.toString()))
                .andExpect(jsonPath("$.data.orderNumber").value(orderNumber))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                .andExpect(jsonPath("$.data.transactionId").value("TXN-abc12345"))
                .andExpect(jsonPath("$.message").value("Payment initiated"));
    }

    @Test
    @DisplayName("GET /api/v1/payments/{id} should return payment and 200")
    void getPaymentShouldReturn200() throws Exception {
        // given
        when(paymentService.getPayment(paymentId)).thenReturn(paymentResponse);

        // when/then
        mockMvc.perform(get("/api/v1/payments/{id}", paymentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(paymentId.toString()))
                .andExpect(jsonPath("$.data.orderNumber").value(orderNumber))
                .andExpect(jsonPath("$.data.amount").value(150))
                .andExpect(jsonPath("$.data.currency").value("TRY"))
                .andExpect(jsonPath("$.data.method").value("CREDIT_CARD"))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("GET /api/v1/payments/order/{orderNumber} should return payment and 200")
    void getPaymentByOrderNumberShouldReturn200() throws Exception {
        // given
        when(paymentService.getPaymentByOrderNumber(orderNumber)).thenReturn(paymentResponse);

        // when/then
        mockMvc.perform(get("/api/v1/payments/order/{orderNumber}", orderNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNumber").value(orderNumber))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                .andExpect(jsonPath("$.data.userId").value(userId.toString()));
    }

    @Test
    @DisplayName("POST /api/v1/payments/{id}/refund should refund payment and return 200")
    void refundPaymentShouldReturn200() throws Exception {
        // given
        PaymentResponse refundedResponse = new PaymentResponse(
                paymentId,
                orderNumber,
                userId,
                BigDecimal.valueOf(150),
                "TRY",
                PaymentMethod.CREDIT_CARD,
                PaymentStatus.REFUNDED,
                "TXN-abc12345",
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(paymentService.refundPayment(paymentId)).thenReturn(refundedResponse);

        // when/then
        mockMvc.perform(post("/api/v1/payments/{id}/refund", paymentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("REFUNDED"))
                .andExpect(jsonPath("$.data.id").value(paymentId.toString()))
                .andExpect(jsonPath("$.message").value("Payment refunded successfully"));
    }
}
