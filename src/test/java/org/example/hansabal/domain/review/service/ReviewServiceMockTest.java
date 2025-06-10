package org.example.hansabal.domain.review.service;

import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.product.repository.ProductRepository;
import org.example.hansabal.domain.review.dto.request.CreateReviewRequest;
import org.example.hansabal.domain.review.dto.response.CreateReviewResponse;
import org.example.hansabal.domain.review.entity.Review;
import org.example.hansabal.domain.review.repository.ReviewRepository;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceMockTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ReviewService reviewService;

    /**
     * <a href="https://it-is-mine.tistory.com/3">네이밍 컨벤션 체크</a>
     */
    @Test
    @DisplayName("리뷰_생성")
    void Given_UserAuth_Then_Create_Success() {
        // given
        // given 1 : 파라미터 셋팅
        Long productId = 1L;
        UserAuth userAuth = new UserAuth(1L, UserRole.USER);
        CreateReviewRequest request = new CreateReviewRequest("테스트 리뷰", 5);

        // given 2 : 응답 결과값 셋팅
        Long reviewId = 1L;
        User user = new User("테스트 이메일", "테스트 비밀번호", "테스트 이름", "테스트 닉네임");

        // Mock
        Review savedReview = Mockito.mock(Review.class);
        given(savedReview.getId()).willReturn(reviewId);
        given(savedReview.getUser()).willReturn(user);
        given(savedReview.getContent()).willReturn(request.getContent());

        // when
        when(userRepository.findByIdOrElseThrow(anyLong())).thenReturn(user);
        when(reviewRepository.save(any())).thenReturn(savedReview);

        // then
        CreateReviewResponse response = reviewService.createReview(productId , userAuth, request);
        assertThat(response.getId()).isEqualTo(reviewId);
        assertThat(response.getNickname()).isEqualTo(user.getNickname());
        assertThat(response.getContent()).isEqualTo(request.getContent());
    }

//        assertThrows(BizException.class, () -> reviewService.createReview(productId , userAuth, request));
//        assertDoesNotThrow(() -> reviewService.createReview(productId , userAuth, request));
}
