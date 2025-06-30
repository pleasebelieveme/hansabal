package org.example.hansabal.domain.review.service;


import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.product.entity.Product;
import org.example.hansabal.domain.product.repository.ProductRepository;
import org.example.hansabal.domain.review.dto.request.CreateReviewRequest;
import org.example.hansabal.domain.review.dto.request.UpdateReviewRequest;
import org.example.hansabal.domain.review.dto.response.CreateReviewResponse;
import org.example.hansabal.domain.review.dto.response.ReviewSimpleResponse;
import org.example.hansabal.domain.review.dto.response.UpdateReviewResponse;
import org.example.hansabal.domain.review.entity.Review;
import org.example.hansabal.domain.review.exception.ReviewErrorCode;
import org.example.hansabal.domain.review.repository.ReviewRepository;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CreateReviewResponse createReview(Long productId, UserAuth userAuth, CreateReviewRequest dto) {
        User user = userRepository.findByIdOrElseThrow(userAuth.getId());
        Product findProduct = productRepository.findByIdOrElseThrow(productId);
        Optional<Review> existingReview = reviewRepository.findByUserAndProductId(user, findProduct.getId());
        if (existingReview.isPresent()) {
            throw new BizException(ReviewErrorCode.REVIEW_ALREADY_EXISTS);
        }
        Review review = new Review(dto.getContent(), dto.getRating(), user, findProduct);
        Review savedReview = reviewRepository.save(review);
        return CreateReviewResponse.from(savedReview);
    }

    @Cacheable(
            value = "reviewsCache",
            key = "#productId + ':' + #page + ':' + #size",//이 속성은 캐싱 동작을 제어하기 위한 조건
            unless = "#result == null || #result.empty()" //unless를 사용하면 지정된 조건이 참일 경우 해당 결과를 캐시에 저장하지 않는다.
    )
    @Transactional(readOnly = true) //페이징
    public Page<ReviewSimpleResponse> getReviews(Long productId, int page, int size) {
        int pageIndex = Math.max(page - 1, 0);
        //이 객체는 페이징 처리에 필요한 정보를 쿼리조건으로 전달한다.
        PageRequest pageRequest = PageRequest.of(pageIndex, size);
        return reviewRepository.findByProductId(productId, pageRequest);
    }

    @Transactional
    public UpdateReviewResponse updateReview(Long reviewId, UpdateReviewRequest request, UserAuth userAuth) {
        Review review = reviewRepository.findByIdOrThrow(reviewId);
        User user = userRepository.findByIdOrElseThrow(userAuth.getId());
        if (!review.getUser().getId().equals(user.getId())) {
            throw new BizException(ReviewErrorCode.REVIEW_FORBIDDEN);
        }
        review.updateReview(request.getContent(), request.getRating());
        return UpdateReviewResponse.from(review);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Review findReview = reviewRepository.findByIdOrThrow(reviewId);
        findReview.softDelete();
    }
}