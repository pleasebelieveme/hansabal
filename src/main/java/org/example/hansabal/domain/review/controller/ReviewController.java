package org.example.hansabal.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.review.dto.request.CreateReviewRequestDto;
import org.example.hansabal.domain.review.dto.response.CreateReviewResponseDto;
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
    public ResponseEntity<CreateReviewResponseDto> createReview(@PathVariable Long productId, @RequestBody CreateReviewRequestDto dto) {

        CreateReviewResponseDto reviewDto = reviewService.createReview(productId,dto.getUserId(),dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(reviewDto);
    }

    @GetMapping()
    public ResponseEntity<List<CreateReviewResponseDto>> getReviews(@PathVariable Long productId) {

        List<CreateReviewResponseDto> findAll = reviewService.findAll(productId);

        return ResponseEntity.status(HttpStatus.OK).body(findAll);
    }
}
