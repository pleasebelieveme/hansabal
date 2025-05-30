package org.example.hansabal.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.review.dto.request.CreateReviewRequest;
import org.example.hansabal.domain.review.dto.request.UpdateReviewRequest;
import org.example.hansabal.domain.review.dto.response.CreateReviewResponse;
import org.example.hansabal.domain.review.dto.response.UpdateReviewResponse;
import org.example.hansabal.domain.review.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/products/{productId}")
    public ResponseEntity<CreateReviewResponse> createReview(@Valid @PathVariable Long productId, @RequestBody CreateReviewRequest request) {

        CreateReviewResponse reviewDto = reviewService.createReview(productId, request.getUserId(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(reviewDto);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<List<CreateReviewResponse>> getReviews(@PathVariable Long productId) {

        List<CreateReviewResponse> findAll = reviewService.findAll(productId);

        return ResponseEntity.status(HttpStatus.OK).body(findAll);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<UpdateReviewResponse> updateReview(@PathVariable Long reviewId, @RequestBody UpdateReviewRequest request) {

        UpdateReviewResponse updateReviewResponseDto = reviewService.updateReview(reviewId, request);

        return ResponseEntity.status(HttpStatus.OK).body(updateReviewResponseDto);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {

        reviewService.deleteReview(reviewId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
