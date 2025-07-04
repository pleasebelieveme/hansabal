package org.example.hansabal.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.review.dto.request.CreateReviewRequest;
import org.example.hansabal.domain.review.dto.request.UpdateReviewRequest;
import org.example.hansabal.domain.review.dto.response.CreateReviewResponse;
import org.example.hansabal.domain.review.dto.response.ReviewPageResult;
import org.example.hansabal.domain.review.dto.response.ReviewSimpleResponse;
import org.example.hansabal.domain.review.dto.response.UpdateReviewResponse;
import org.example.hansabal.domain.review.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/products/{productId}") //리뷰생성
    public ResponseEntity<CreateReviewResponse> createReview(
            @AuthenticationPrincipal UserAuth userAuth,
            @PathVariable Long productId,
            @Valid @RequestBody CreateReviewRequest request) {
        CreateReviewResponse reviewDto = reviewService.createReview(productId, userAuth, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewDto);
    }

    @GetMapping("products/{productId}") // 리뷰 페이징
    public ResponseEntity<ReviewPageResult> getReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ReviewPageResult reviews = reviewService.getReviews(productId, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(reviews);
    }

    @PutMapping("/{reviewId}") //리뷰 수정
    public ResponseEntity<UpdateReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @RequestBody @Valid UpdateReviewRequest request,
            @AuthenticationPrincipal UserAuth userAuth
    ) {
        UpdateReviewResponse updateReviewResponseDto = reviewService.updateReview(reviewId, request, userAuth);
        return ResponseEntity.status(HttpStatus.OK).body(updateReviewResponseDto);
    }

    @DeleteMapping("/{reviewId}") //소프트 delete
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
