package org.example.hansabal.domain.review.service;

import org.example.hansabal.domain.review.dto.request.CreateReviewRequest;
import org.example.hansabal.domain.review.dto.request.UpdateReviewRequest;
import org.example.hansabal.domain.review.dto.response.CreateReviewResponse;
import org.example.hansabal.domain.review.dto.response.ReviewResponse;
import org.example.hansabal.domain.review.dto.response.UpdateReviewResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReviewService {
    CreateReviewResponse createReview(Long productId, Long userId, CreateReviewRequest request);

    List<CreateReviewResponse> findAll(Long productId);

    UpdateReviewResponse updateReview(Long reviewId, UpdateReviewRequest request);

    void deleteReview(Long reviewId);

    Page<ReviewResponse> getReviews(Long productId, int page, int size);
}
