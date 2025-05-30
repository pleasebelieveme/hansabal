package org.example.hansabal.domain.review.service;

import org.example.hansabal.domain.review.dto.request.CreateReviewRequestDto;
import org.example.hansabal.domain.review.dto.request.UpdateReviewRequestDto;
import org.example.hansabal.domain.review.dto.response.CreateReviewResponseDto;
import org.example.hansabal.domain.review.dto.response.UpdateReviewResponseDto;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    CreateReviewResponseDto createReview(Long productId, Long userId, CreateReviewRequestDto request);

    List<CreateReviewResponseDto> findAll(Long productId);

    UpdateReviewResponseDto updateReview(Long reviewId, UpdateReviewRequestDto request);

    void deleteReview(Long reviewId);
}
