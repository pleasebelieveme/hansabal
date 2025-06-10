package org.example.hansabal.domain.review.service;

import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.product.entity.Product;
import org.example.hansabal.domain.product.entity.ProductStatus;
import org.example.hansabal.domain.product.repository.ProductRepository;
import org.example.hansabal.domain.review.dto.request.CreateReviewRequest;
import org.example.hansabal.domain.review.dto.response.CreateReviewResponse;
import org.example.hansabal.domain.review.entity.Review;
import org.example.hansabal.domain.review.repository.ReviewRepository;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.repository.UserRepository;
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

    //@Mock: ReviewService가 의존하는
    // ReviewRepository, UserRepository, ProductRepository 등의 Mock 객체를 선언합니다
    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    //Test 대상 클래스(ReviewService)의 인스턴스를 생성하며, @Mock으로 선언된 가짜 객체들이 의존성으로 주입됩니다.
    @InjectMocks
    private ReviewService reviewService;

    @Test
    void 리뷰생성() {
        // given 1 : 파라미터 셋팅
        Long productId = 1L;
        UserAuth userAuth = new UserAuth(1L, UserRole.USER);
        CreateReviewRequest request = new CreateReviewRequest("테스트 리뷰", 5);
        Product product = new Product("테스트 제품", 10, ProductStatus.FOR_SALE);
        // given 2 : 테스트 결과 예상값 셋팅
        Long reviewId = 1L;
        User user = new User("테스트 이메일", "테스트 비밀번호", "테스트 이름", "테스트 닉네임");

        // Mock review (가짜)객체를 생성한다.
        // given() 메서드를 사용하는 이유 : Mock 객체는 명시적으로 동작을 설정하지 않으면 아무런 동작도 하지 않습니다.
        Review review = Mockito.mock(Review.class);
        given(review.getId()).willReturn(reviewId);
        given(review.getUser()).willReturn(user);
        given(review.getContent()).willReturn(request.getContent());

        // when
        when(productRepository.findByIdOrElseThrow(anyLong())).thenReturn(product);
        when(userRepository.findByIdOrElseThrow(anyLong())).thenReturn(user);
        when(reviewRepository.save(any())).thenReturn(review);
        CreateReviewResponse response = reviewService.createReview(productId, userAuth, request);

        // then
        assertThat(response.getId()).isEqualTo(reviewId);
        assertThat(response.getNickname()).isEqualTo(user.getNickname());
        assertThat(response.getContent()).isEqualTo(request.getContent());
    }

    // 예외 test 할때 사용할 예정참고할 코드 입니다. 추후에 삭제하겠습니다.
    // assertThrows(BizException .class, () -> reviewService.createReview(productId , userAuth, request));
    // assertDoesNotThrow(() -> reviewService.createReview(productId , userAuth, request));
    @Test
    void 조회() {

    }
}
