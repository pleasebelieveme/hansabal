package org.example.hansabal.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.review.dto.request.CreateReviewRequestDto;
import org.example.hansabal.domain.review.dto.request.UpdateReviewRequestDto;
import org.example.hansabal.domain.review.dto.response.CreateReviewResponseDto;
import org.example.hansabal.domain.review.dto.response.UpdateReviewResponseDto;
import org.example.hansabal.domain.review.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product/{productId}/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping()
    public ResponseEntity<CreateReviewResponseDto> createReview(@PathVariable Long productId, @RequestBody CreateReviewRequestDto request) {

        CreateReviewResponseDto reviewDto = reviewService.createReview(productId, request.getUserId(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(reviewDto);
    }

    @GetMapping()
    public ResponseEntity<List<CreateReviewResponseDto>> getReviews(@PathVariable Long productId) {

        List<CreateReviewResponseDto> findAll = reviewService.findAll(productId);

        return ResponseEntity.status(HttpStatus.OK).body(findAll);
    }


    @PutMapping("/{reviewId}")
    public ResponseEntity<UpdateReviewResponseDto> updateReview(@PathVariable Long reviewId, @PathVariable Long productId, @RequestBody UpdateReviewRequestDto request) {
   

        UpdateReviewResponseDto updateReviewResponseDto = reviewService.updateReview(reviewId, request);

        return ResponseEntity.status(HttpStatus.OK).body(updateReviewResponseDto);
    }


    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId, @PathVariable Long productId) {

        reviewService.deleteReview(reviewId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
