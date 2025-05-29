package org.example.hansabal.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.review.apiresponse.ApiResponseDto;
import org.example.hansabal.domain.review.apiresponse.SuccessCode;
import org.example.hansabal.domain.review.dto.request.ReviewRequestDto;
import org.example.hansabal.domain.review.dto.response.ReviewResponseDto;
import org.example.hansabal.domain.review.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product/{productId}/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    public ResponseEntity<ApiResponseDto<ReviewResponseDto>> createReview(@PathVariable Long productId,  @RequestBody ReviewRequestDto dto) {

        ReviewResponseDto reviewDto = reviewService.createReview(productId,dto);

        return ResponseEntity.ok(ApiResponseDto.success(SuccessCode.REVIEW_CREATE_SUCCESS, reviewDto));
    }
}
