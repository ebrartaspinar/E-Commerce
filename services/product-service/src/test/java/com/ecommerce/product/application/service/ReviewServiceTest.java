package com.ecommerce.product.application.service;

import com.ecommerce.common.dto.PagedResponse;
import com.ecommerce.common.exception.DuplicateResourceException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.product.application.dto.CreateReviewRequest;
import com.ecommerce.product.application.dto.ReviewResponse;
import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.model.ProductStatus;
import com.ecommerce.product.domain.model.Review;
import com.ecommerce.product.domain.model.ReviewStatus;
import com.ecommerce.product.domain.repository.ProductRepository;
import com.ecommerce.product.domain.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewService Unit Tests")
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ReviewService reviewService;

    private UUID productId;
    private UUID userId;
    private UUID reviewId;
    private Product product;
    private Review review;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        userId = UUID.randomUUID();
        reviewId = UUID.randomUUID();

        product = Product.builder()
                .sellerId(UUID.randomUUID())
                .name("Test Product")
                .description("Description")
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .sku("SKU-001")
                .status(ProductStatus.ACTIVE)
                .brand("TestBrand")
                .averageRating(0.0)
                .reviewCount(0)
                .images(new ArrayList<>())
                .build();
        product.setId(productId);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        review = Review.builder()
                .productId(productId)
                .userId(userId)
                .rating(4)
                .comment("Great product!")
                .status(ReviewStatus.APPROVED)
                .build();
        review.setId(reviewId);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("getProductReviews")
    class GetProductReviews {

        @Test
        @DisplayName("Should return only approved reviews for a product")
        void shouldReturnOnlyApprovedReviews() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            Page<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);

            when(reviewRepository.findByProductIdAndStatus(productId, ReviewStatus.APPROVED, pageable))
                    .thenReturn(reviewPage);

            // when
            PagedResponse<ReviewResponse> result = reviewService.getProductReviews(productId, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).rating()).isEqualTo(4);
            assertThat(result.content().get(0).comment()).isEqualTo("Great product!");
            assertThat(result.content().get(0).status()).isEqualTo(ReviewStatus.APPROVED);
            verify(reviewRepository).findByProductIdAndStatus(productId, ReviewStatus.APPROVED, pageable);
        }

        @Test
        @DisplayName("Should return empty page when product has no approved reviews")
        void shouldReturnEmptyPageWhenNoApprovedReviews() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            Page<Review> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(reviewRepository.findByProductIdAndStatus(productId, ReviewStatus.APPROVED, pageable))
                    .thenReturn(emptyPage);

            // when
            PagedResponse<ReviewResponse> result = reviewService.getProductReviews(productId, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should return multiple reviews with correct paging")
        void shouldReturnMultipleReviewsWithCorrectPaging() {
            // given
            Pageable pageable = PageRequest.of(0, 20);

            Review review2 = Review.builder()
                    .productId(productId)
                    .userId(UUID.randomUUID())
                    .rating(5)
                    .comment("Excellent!")
                    .status(ReviewStatus.APPROVED)
                    .build();
            review2.setId(UUID.randomUUID());
            review2.setCreatedAt(LocalDateTime.now());
            review2.setUpdatedAt(LocalDateTime.now());

            Page<Review> reviewPage = new PageImpl<>(List.of(review, review2), pageable, 2);

            when(reviewRepository.findByProductIdAndStatus(productId, ReviewStatus.APPROVED, pageable))
                    .thenReturn(reviewPage);

            // when
            PagedResponse<ReviewResponse> result = reviewService.getProductReviews(productId, pageable);

            // then
            assertThat(result.content()).hasSize(2);
            assertThat(result.totalElements()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("createReview")
    class CreateReview {

        @Test
        @DisplayName("Should create review successfully")
        void shouldCreateReviewSuccessfully() {
            // given
            CreateReviewRequest request = new CreateReviewRequest(5, "Amazing product!");

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(reviewRepository.findByProductIdAndUserId(productId, userId)).thenReturn(Optional.empty());
            when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
                Review saved = invocation.getArgument(0);
                saved.setId(UUID.randomUUID());
                saved.setCreatedAt(LocalDateTime.now());
                saved.setUpdatedAt(LocalDateTime.now());
                return saved;
            });
            when(reviewRepository.countByProductIdAndStatus(productId, ReviewStatus.APPROVED)).thenReturn(1L);

            Page<Review> approvedReviewsPage = new PageImpl<>(List.of(
                    Review.builder().productId(productId).userId(userId).rating(5).status(ReviewStatus.APPROVED).build()
            ));
            when(reviewRepository.findByProductIdAndStatus(eq(productId), eq(ReviewStatus.APPROVED), eq(Pageable.unpaged())))
                    .thenReturn(approvedReviewsPage);
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            ReviewResponse result = reviewService.createReview(userId, productId, request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.rating()).isEqualTo(5);
            assertThat(result.comment()).isEqualTo("Amazing product!");
            assertThat(result.status()).isEqualTo(ReviewStatus.APPROVED);
            assertThat(result.productId()).isEqualTo(productId);
            assertThat(result.userId()).isEqualTo(userId);

            verify(productRepository).findById(productId);
            verify(reviewRepository).findByProductIdAndUserId(productId, userId);
            verify(reviewRepository).save(any(Review.class));
        }

        @Test
        @DisplayName("Should throw DuplicateResourceException when user already reviewed the product")
        void shouldThrowDuplicateResourceExceptionWhenDuplicateReview() {
            // given
            CreateReviewRequest request = new CreateReviewRequest(3, "Duplicate review");

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(reviewRepository.findByProductIdAndUserId(productId, userId)).thenReturn(Optional.of(review));

            // when / then
            assertThatThrownBy(() -> reviewService.createReview(userId, productId, request))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("already reviewed");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when product not found")
        void shouldThrowResourceNotFoundExceptionWhenProductNotFound() {
            // given
            UUID nonExistentProductId = UUID.randomUUID();
            CreateReviewRequest request = new CreateReviewRequest(4, "Good product");

            when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> reviewService.createReview(userId, nonExistentProductId, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Product");
        }
    }

    @Nested
    @DisplayName("Rating Recalculation")
    class RatingRecalculation {

        @Test
        @DisplayName("Should recalculate product average rating after new review")
        void shouldRecalculateProductAverageRatingAfterNewReview() {
            // given
            CreateReviewRequest request = new CreateReviewRequest(4, "Good product");

            Review existingReview = Review.builder()
                    .productId(productId)
                    .userId(UUID.randomUUID())
                    .rating(5)
                    .status(ReviewStatus.APPROVED)
                    .build();
            existingReview.setId(UUID.randomUUID());
            existingReview.setCreatedAt(LocalDateTime.now());

            Review newReview = Review.builder()
                    .productId(productId)
                    .userId(userId)
                    .rating(4)
                    .status(ReviewStatus.APPROVED)
                    .build();
            newReview.setId(UUID.randomUUID());
            newReview.setCreatedAt(LocalDateTime.now());

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(reviewRepository.findByProductIdAndUserId(productId, userId)).thenReturn(Optional.empty());
            when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
                Review saved = invocation.getArgument(0);
                saved.setId(UUID.randomUUID());
                saved.setCreatedAt(LocalDateTime.now());
                saved.setUpdatedAt(LocalDateTime.now());
                return saved;
            });
            when(reviewRepository.countByProductIdAndStatus(productId, ReviewStatus.APPROVED)).thenReturn(2L);

            Page<Review> approvedReviewsPage = new PageImpl<>(List.of(existingReview, newReview));
            when(reviewRepository.findByProductIdAndStatus(eq(productId), eq(ReviewStatus.APPROVED), eq(Pageable.unpaged())))
                    .thenReturn(approvedReviewsPage);
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            reviewService.createReview(userId, productId, request);

            // then
            verify(productRepository).save(any(Product.class));
            // Average of 5 and 4 = 4.5
            assertThat(product.getAverageRating()).isEqualTo(4.5);
            assertThat(product.getReviewCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("Should set zero rating when no approved reviews exist")
        void shouldSetZeroRatingWhenNoApprovedReviewsExist() {
            // given
            CreateReviewRequest request = new CreateReviewRequest(3, "Ok product");

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(reviewRepository.findByProductIdAndUserId(productId, userId)).thenReturn(Optional.empty());
            when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
                Review saved = invocation.getArgument(0);
                saved.setId(UUID.randomUUID());
                saved.setCreatedAt(LocalDateTime.now());
                saved.setUpdatedAt(LocalDateTime.now());
                return saved;
            });
            when(reviewRepository.countByProductIdAndStatus(productId, ReviewStatus.APPROVED)).thenReturn(0L);
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            reviewService.createReview(userId, productId, request);

            // then
            assertThat(product.getAverageRating()).isEqualTo(0.0);
            assertThat(product.getReviewCount()).isEqualTo(0);
        }
    }
}
