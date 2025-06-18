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
import org.example.hansabal.domain.review.dto.request.UpdateReviewRequest;
import org.example.hansabal.domain.review.dto.response.CreateReviewResponse;
import org.example.hansabal.domain.review.dto.response.UpdateReviewResponse;
import org.example.hansabal.domain.review.entity.Review;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
@Testcontainers
@ActiveProfiles("test")
@Sql(scripts = {"/review_test_db.sql","/review_user_test_db.sql"},executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewRepository reviewRepository;



    @BeforeEach
    void setUp() {
        UserCreateRequest userRequest = new UserCreateRequest("test@email.com", "!Aa123456", "테스트이름", "테스트닉네임", UserRole.USER);
        userService.createUser(userRequest);
        ProductRequestDto productRequest = new ProductRequestDto("테스트제품");
        productService.createProduct(productRequest);
    }

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");
    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
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
        assertThat(response.getId().equals(1L));
        assertThat(response.getNickname().equals("테스트닉네임"));
        assertThat(response.getContent().equals("테스트 리뷰"));
        assertThat(response.getRating().equals(5));
        assertThat(response).isNotNull();
    }

    @Test
    void 리뷰_중복생성_예외() {
        //given
        Long productId = 1L;
        UserAuth userAuth = new UserAuth(2L, UserRole.USER);
        CreateReviewRequest request = new CreateReviewRequest("테스트 리뷰", 5);

        //when
        reviewService.createReview(productId, userAuth, request);

        //then
        assertThatThrownBy(() -> reviewService.createReview(productId, userAuth, request))
                .hasMessageContaining("해당 상품에 대한 리뷰를 이미 작성하셨습니다.");
    }

    @Test
    void 리뷰수정() {
        //given
        Long productId = 1L;
        Long reviewId = 1L;
        UserAuth userAuth = new UserAuth(2L, UserRole.USER);
        CreateReviewRequest createReviewRequest = new CreateReviewRequest("테스트 리뷰", 5);

        reviewService.createReview(productId, userAuth, createReviewRequest);
        UpdateReviewRequest updateReviewRequest = new UpdateReviewRequest("테스트 업데이트 리뷰", 4);

        //when
        UpdateReviewResponse response = reviewService.updateReview(reviewId, updateReviewRequest, userAuth);

        //then
        assertThat(response.getId().equals(reviewId));
        assertThat(response.getNickname().equals("테스트닉네임"));
        assertThat(response.getContent().equals("테스트 업데이트 리뷰"));
        assertThat(response.getRating().equals(4));
        assertThat(response).isNotNull();
    }

    @Test
    void 리뷰수정_유져정보_불일치_예외() {
        //given
        Long productId = 1L;
        Long reviewId = 1L;
        UserAuth userAuth = new UserAuth(1L, UserRole.USER);
        UserAuth otherUserAuth = new UserAuth(2L, UserRole.USER);
        CreateReviewRequest createReviewRequest = new CreateReviewRequest("테스트 리뷰", 5);

        //유저를 하나 더 생성
        UserCreateRequest userRequest = new UserCreateRequest("test@email2.com", "!Aa1234562", "테스트이름2", "테스트닉네임2", UserRole.USER);
        userService.createUser(userRequest);

        reviewService.createReview(productId, userAuth, createReviewRequest);
        UpdateReviewRequest updateReviewRequest = new UpdateReviewRequest("테스트 업데이트 리뷰", 4);

        //then,then
        assertThatThrownBy(() -> reviewService.updateReview(reviewId, updateReviewRequest, otherUserAuth))
                .hasMessageContaining("권한이 없습니다.");
    }

    @Test
    void 리뷰삭제() {
        //given
        Long productId = 1L;
        UserAuth userAuth = new UserAuth(1L, UserRole.USER);
        CreateReviewRequest request = new CreateReviewRequest("테스트 리뷰", 5);
        CreateReviewResponse response = reviewService.createReview(productId, userAuth, request);

        //when
        reviewService.deleteReview(response.getId());
        Optional<Review> review = reviewRepository.findById(response.getId());

        //then
        assertThat(review).isNotEmpty();
        assertThat(review.get().getDeletedAt()).isNotNull();
    }
}
