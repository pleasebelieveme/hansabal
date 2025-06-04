package org.example.hansabal.domain.review.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.example.hansabal.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
   @Query("SELECT r FROM Review r JOIN FETCH r.product WHERE r.product.id= :productId")
    Page<Review> findReviewsByProductId(@Param("productId") Long productId, Pageable pageable);
}
