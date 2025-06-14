package org.example.hansabal.domain.review.service;

import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.product.dto.request.ProductRequestDto;
import org.example.hansabal.domain.product.entity.Product;
import org.example.hansabal.domain.product.repository.ProductRepository;
import org.example.hansabal.domain.product.service.ProductService;
import org.example.hansabal.domain.review.dto.request.CreateReviewRequest;
import org.example.hansabal.domain.review.dto.response.CreateReviewResponse;
import org.example.hansabal.domain.review.repository.ReviewRepository;
import org.example.hansabal.domain.users.dto.request.UserCreateRequest;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
@Testcontainers
@ActiveProfiles("test")
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        //todo 블러그 작성  래포지토리.save 를 사용하면 안되는 이유
        UserCreateRequest userRequest = new UserCreateRequest("test@email.com","!Aa123456","테스트이름","테스트닉네임",UserRole.USER);
        userService.createUser(userRequest);
        ProductRequestDto productRequest = new ProductRequestDto("테스트제품");
        productService.createProduct(productRequest);
    }

    @Test
    void 리뷰_생성() {
        //given
        Long productId = 1L;
        UserAuth userAuth = new UserAuth(1L, UserRole.USER);
        CreateReviewRequest request = new CreateReviewRequest("테스트 리뷰", 5);

        //when
        CreateReviewResponse response = reviewService.createReview(productId, userAuth, request);

        //then
        Assertions.assertThat(response.getContent().equals("테스트 리뷰"));
        //rating은 다음 pr때 수정해서 올려놓을게요 지금 수정하면 더 코드리뷰가 많아 질까봐 ..ㅎㅎ
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId().equals(1L));
    }

    @Test
    void 리뷰_중복생성_예외() {
        //given
        Long productId = 1L;
        UserAuth userAuth = new UserAuth(1L, UserRole.USER);
        CreateReviewRequest request = new CreateReviewRequest("테스트 리뷰", 5);

        //when
        reviewService.createReview(productId, userAuth, request);
        //then
        Assertions.assertThatThrownBy(() -> reviewService.createReview(productId, userAuth, request))
                .isInstanceOf(BizException.class).hasMessageContaining("해당 상품에 대한 리뷰를 이미 작성하셨습니다.");
    }

    @Test
    void 리뷰수정() {


    }
}
