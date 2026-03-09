package com.ecommerce.product.application.service;

import com.ecommerce.common.dto.PagedResponse;
import com.ecommerce.common.exception.BusinessRuleException;
import com.ecommerce.common.exception.DuplicateResourceException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.product.application.dto.CreateReviewRequest;
import com.ecommerce.product.application.dto.ReviewResponse;
import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.model.Review;
import com.ecommerce.product.domain.model.ReviewStatus;
import com.ecommerce.product.domain.repository.ProductRepository;
import com.ecommerce.product.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public PagedResponse<ReviewResponse> getProductReviews(UUID productId, Pageable pageable) {
        Page<ReviewResponse> page = reviewRepository
                .findByProductIdAndStatus(productId, ReviewStatus.APPROVED, pageable)
                .map(this::mapToResponse);
        return PagedResponse.from(page);
    }

    @Transactional
    public ReviewResponse createReview(UUID userId, UUID productId, CreateReviewRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        reviewRepository.findByProductIdAndUserId(productId, userId)
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("You have already reviewed this product");
                });

        Review review = Review.builder()
                .productId(productId)
                .userId(userId)
                .rating(request.rating())
                .comment(request.comment())
                .status(ReviewStatus.APPROVED)
                .build();

        Review savedReview = reviewRepository.save(review);
        log.info("Review created: id={}, productId={}, userId={}, rating={}", savedReview.getId(), productId, userId, request.rating());

        updateProductRating(product);

        return mapToResponse(savedReview);
    }

    private void updateProductRating(Product product) {
        long approvedCount = reviewRepository.countByProductIdAndStatus(product.getId(), ReviewStatus.APPROVED);

        if (approvedCount == 0) {
            product.setAverageRating(0.0);
            product.setReviewCount(0);
        } else {
            Page<Review> allApprovedReviews = reviewRepository.findByProductIdAndStatus(
                    product.getId(), ReviewStatus.APPROVED, Pageable.unpaged());

            double averageRating = allApprovedReviews.getContent().stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);

            product.setAverageRating(Math.round(averageRating * 10.0) / 10.0);
            product.setReviewCount((int) approvedCount);
        }

        productRepository.save(product);
    }

    private ReviewResponse mapToResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getProductId(),
                review.getUserId(),
                review.getRating(),
                review.getComment(),
                review.getStatus(),
                review.getCreatedAt()
        );
    }
}
