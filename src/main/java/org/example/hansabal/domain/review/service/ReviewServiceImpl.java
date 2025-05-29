package org.example.hansabal.domain.review.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.product.entity.Product;
import org.example.hansabal.domain.product.repository.ProductRepository;
import org.example.hansabal.domain.review.dto.request.CreateReviewRequestDto;
import org.example.hansabal.domain.review.dto.response.CreateReviewResponseDto;
import org.example.hansabal.domain.review.entity.Review;
import org.example.hansabal.domain.review.repository.ReviewRepository;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    @Override
    public CreateReviewResponseDto createReview(Long productId, Long userId, CreateReviewRequestDto dto) {

        User findUser = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ""));

        //일단 유저가 있는지 확인한다.
        Product findProduct = productRepository.findById(productId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " 리뷰유저가 없습니다."));

        //엔티티에 정보를 넣어준다.
        Review review = new Review(findUser.getNickname(), findUser, findProduct);

        //엔티티에 넣은 정보를 DB에 넣어준다.
        Review savedReview = reviewRepository.save(review);

        //다시 DB에 있는 데이터를 이용해 반환한다.
        return CreateReviewResponseDto.from(savedReview);
    }
}
