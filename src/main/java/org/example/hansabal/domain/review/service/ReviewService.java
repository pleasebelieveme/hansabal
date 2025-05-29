package org.example.hansabal.domain.review.service;

import org.example.hansabal.domain.review.dto.request.ReviewRequestDto;
import org.example.hansabal.domain.review.dto.response.ReviewResponseDto;

public interface ReviewService {
    ReviewResponseDto createReview(Long productId, ReviewRequestDto dto);
}
