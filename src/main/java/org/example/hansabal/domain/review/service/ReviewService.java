package org.example.hansabal.domain.review.service;

import jakarta.validation.Valid;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.review.dto.request.CreateReviewRequest;
import org.example.hansabal.domain.review.dto.request.UpdateReviewRequest;
import org.example.hansabal.domain.review.dto.response.CreateReviewResponse;
import org.example.hansabal.domain.review.dto.response.ReviewResponse;
import org.example.hansabal.domain.review.dto.response.UpdateReviewResponse;
import org.springframework.data.domain.Page;


public interface ReviewService {
    CreateReviewResponse createReview(@Valid Long productId, UserAuth userAuth, CreateReviewRequest request);

    UpdateReviewResponse updateReview(Long reviewId, @Valid UpdateReviewRequest request, UserAuth userAuth);

    void deleteReview(Long reviewId);

    Page<ReviewResponse> getReviews(Long productId, int page, int size);
}
