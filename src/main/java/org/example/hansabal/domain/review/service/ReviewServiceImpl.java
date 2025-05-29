package org.example.hansabal.domain.review.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.review.dto.request.ReviewRequestDto;
import org.example.hansabal.domain.review.dto.response.ReviewResponseDto;
import org.example.hansabal.domain.review.entity.Review;
import org.example.hansabal.domain.review.repository.ReviewRepository;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;


    @Override
    public ReviewResponseDto createReview(Long productId, ReviewRequestDto dto) {

        //일단 유저가 있는지 확인한다.
        User findUser = userRepository.findById(productId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "삭제할 리뷰유저가 없습니다."));

        //엔티티에 정보를 넣어준다.
        Review review = new Review(findUser.getNickname(), findUser);

        //엔티티에 넣은 정보를 DB에 넣어준다.
        Review savedReview = reviewRepository.save(review);

        //다시 DB에 있는 데이터를 이용해 반환한다.
        return new ReviewResponseDto(savedReview.getUser().getNickname(), savedReview.getContent());
    }
}
