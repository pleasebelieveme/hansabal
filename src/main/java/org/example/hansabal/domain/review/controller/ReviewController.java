package org.example.hansabal.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.review.dto.request.createReviewRequestDto;
import org.example.hansabal.domain.review.dto.response.createReviewResponseDto;
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
    public ResponseEntity<createReviewResponseDto> createReview(@PathVariable Long productId, @RequestBody createReviewRequestDto dto) {

        createReviewResponseDto reviewDto = reviewService.createReview(productId,dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(reviewDto);
    }

    @GetMapping()
    public ResponseEntity<List<createReviewResponseDto>> getReviews(@PathVariable Long productId) {

        reviewService.findAll(productId);

        return ResponseEntity.status(HttpStatus.OK).body();
    }
}
