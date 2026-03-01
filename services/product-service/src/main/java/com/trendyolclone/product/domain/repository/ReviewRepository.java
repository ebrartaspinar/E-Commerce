package com.trendyolclone.product.domain.repository;

import com.trendyolclone.product.domain.model.Review;
import com.trendyolclone.product.domain.model.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Page<Review> findByProductIdAndStatus(UUID productId, ReviewStatus status, Pageable pageable);

    Optional<Review> findByProductIdAndUserId(UUID productId, UUID userId);

    long countByProductIdAndStatus(UUID productId, ReviewStatus status);
}
