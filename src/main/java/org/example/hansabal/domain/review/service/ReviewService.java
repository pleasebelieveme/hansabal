package org.example.hansabal.domain.review.service;

import org.example.hansabal.domain.review.dto.request.CreateReviewRequestDto;
import org.example.hansabal.domain.review.dto.request.UpdateReviewRequestDto;
import org.example.hansabal.domain.review.dto.response.CreateReviewResponseDto;
import org.example.hansabal.domain.review.dto.response.UpdateReviewResponseDto;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    CreateReviewResponse createReview(Long productId, Long userId, CreateReviewRequest request);

    List<CreateReviewResponse> findAll(Long productId);

    UpdateReviewResponse updateReview(Long reviewId, UpdateReviewRequest request);

    void deleteReview(Long reviewId);
}
