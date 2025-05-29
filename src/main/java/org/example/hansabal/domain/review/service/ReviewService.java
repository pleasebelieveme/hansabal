package org.example.hansabal.domain.review.service;

import org.example.hansabal.domain.review.dto.request.CreateReviewRequestDto;
import org.example.hansabal.domain.review.dto.response.CreateReviewResponseDto;

import java.util.List;

public interface ReviewService {
    CreateReviewResponseDto createReview(Long productId, Long userId, CreateReviewRequestDto dto);

    List<CreateReviewResponseDto> findAll(Long productId);
}
