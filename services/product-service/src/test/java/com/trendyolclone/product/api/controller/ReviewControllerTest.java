package com.trendyolclone.product.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trendyolclone.common.dto.PagedResponse;
import com.trendyolclone.common.exception.DuplicateResourceException;
import com.trendyolclone.common.exception.ResourceNotFoundException;
import com.trendyolclone.product.application.dto.CreateReviewRequest;
import com.trendyolclone.product.application.dto.ReviewResponse;
import com.trendyolclone.product.application.service.ReviewService;
import com.trendyolclone.product.domain.model.ReviewStatus;
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
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.trendyolclone.product.infrastructure.config.SecurityConfig;
import com.trendyolclone.product.infrastructure.config.JwtAuthenticationFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = ReviewController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ReviewController Integration Tests")
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private ReviewService reviewService;

    private UUID productId;
    private UUID userId;
    private UUID reviewId;
    private ReviewResponse sampleReview;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        userId = UUID.randomUUID();
        reviewId = UUID.randomUUID();

        sampleReview = new ReviewResponse(
                reviewId, productId, userId, 4, "Great product!",
                ReviewStatus.APPROVED, LocalDateTime.now()
        );

        // Set up security context for POST requests
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId.toString(), null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Nested
    @DisplayName("GET /api/v1/products/{productId}/reviews")
    class GetProductReviews {

        @Test
        @DisplayName("Should return 200 with product reviews")
        void shouldReturn200WithProductReviews() throws Exception {
            // given
            PagedResponse<ReviewResponse> pagedResponse = new PagedResponse<>(
                    List.of(sampleReview), 0, 20, 1, 1, true, true
            );

            when(reviewService.getProductReviews(eq(productId), any()))
                    .thenReturn(pagedResponse);

            // when / then
            mockMvc.perform(get("/api/v1/products/{productId}/reviews", productId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].rating").value(4))
                    .andExpect(jsonPath("$.data.content[0].comment").value("Great product!"))
                    .andExpect(jsonPath("$.data.content[0].status").value("APPROVED"))
                    .andExpect(jsonPath("$.data.totalElements").value(1));
        }

        @Test
        @DisplayName("Should return 200 with empty reviews when none exist")
        void shouldReturn200WithEmptyReviewsWhenNoneExist() throws Exception {
            // given
            PagedResponse<ReviewResponse> emptyResponse = new PagedResponse<>(
                    Collections.emptyList(), 0, 20, 0, 0, true, true
            );

            when(reviewService.getProductReviews(eq(productId), any()))
                    .thenReturn(emptyResponse);

            // when / then
            mockMvc.perform(get("/api/v1/products/{productId}/reviews", productId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }

        @Test
        @DisplayName("Should return 200 with multiple reviews")
        void shouldReturn200WithMultipleReviews() throws Exception {
            // given
            ReviewResponse review2 = new ReviewResponse(
                    UUID.randomUUID(), productId, UUID.randomUUID(), 5,
                    "Excellent!", ReviewStatus.APPROVED, LocalDateTime.now()
            );

            PagedResponse<ReviewResponse> pagedResponse = new PagedResponse<>(
                    List.of(sampleReview, review2), 0, 20, 2, 1, true, true
            );

            when(reviewService.getProductReviews(eq(productId), any()))
                    .thenReturn(pagedResponse);

            // when / then
            mockMvc.perform(get("/api/v1/products/{productId}/reviews", productId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content.length()").value(2))
                    .andExpect(jsonPath("$.data.totalElements").value(2));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/products/{productId}/reviews")
    class CreateReview {

        @Test
        @DisplayName("Should return 201 when review is created successfully")
        void shouldReturn201WhenReviewCreatedSuccessfully() throws Exception {
            // given
            CreateReviewRequest request = new CreateReviewRequest(5, "Amazing product!");
            ReviewResponse createdReview = new ReviewResponse(
                    UUID.randomUUID(), productId, userId, 5, "Amazing product!",
                    ReviewStatus.APPROVED, LocalDateTime.now()
            );

            when(reviewService.createReview(eq(userId), eq(productId), any(CreateReviewRequest.class)))
                    .thenReturn(createdReview);

            // when / then
            mockMvc.perform(post("/api/v1/products/{productId}/reviews", productId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.rating").value(5))
                    .andExpect(jsonPath("$.data.comment").value("Amazing product!"))
                    .andExpect(jsonPath("$.data.status").value("APPROVED"))
                    .andExpect(jsonPath("$.message").value("Review created successfully"));
        }

        @Test
        @DisplayName("Should return 404 when product not found for review")
        void shouldReturn404WhenProductNotFoundForReview() throws Exception {
            // given
            UUID nonExistentProductId = UUID.randomUUID();
            CreateReviewRequest request = new CreateReviewRequest(3, "Good");

            when(reviewService.createReview(eq(userId), eq(nonExistentProductId), any(CreateReviewRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Product", "id", nonExistentProductId));

            // when / then
            mockMvc.perform(post("/api/v1/products/{productId}/reviews", nonExistentProductId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 409 when user already reviewed the product")
        void shouldReturn409WhenDuplicateReview() throws Exception {
            // given
            CreateReviewRequest request = new CreateReviewRequest(4, "Another review");

            when(reviewService.createReview(eq(userId), eq(productId), any(CreateReviewRequest.class)))
                    .thenThrow(new DuplicateResourceException("You have already reviewed this product"));

            // when / then
            mockMvc.perform(post("/api/v1/products/{productId}/reviews", productId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }
}
