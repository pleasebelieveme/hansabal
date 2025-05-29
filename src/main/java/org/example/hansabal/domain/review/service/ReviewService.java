package org.example.hansabal.domain.review.service;

import org.example.hansabal.domain.review.dto.request.createReviewRequestDto;
import org.example.hansabal.domain.review.dto.response.createReviewResponseDto;

import java.util.List;

public interface ReviewService {
    createReviewResponseDto createReview(Long productId, createReviewRequestDto dto);

    List<createReviewResponseDto> findAll(Long productId);
}
