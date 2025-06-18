package org.example.hansabal.domain.review.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.review.entity.Review;
import org.example.hansabal.domain.review.exception.ReviewErrorCode;
import org.example.hansabal.domain.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = "product")
    @Query(value = "SELECT r FROM Review r WHERE r.product.id= :productId",
    countQuery = "SELECT count(r) FROM Review r WHERE r.product.id= :productId")

    Page<Review> findReviewsByProductId(@Param("productId") Long productId, Pageable pageable);

    default Review findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new BizException(ReviewErrorCode.REVIEW_NOT_FOUND));
    }

    Optional<Review> findByProductId(Long productId);

    default Review findByProductIdOrThrow(Long productId) {
        return findByProductId(productId).orElseThrow(()->new BizException(ReviewErrorCode.RIVIEW_NOT_FOUND_PRODUCT));
    }

    Optional<Review> findByUserAndProductId(User user, Long productId);
}
