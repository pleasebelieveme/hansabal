package org.example.hansabal.domain.review.service;

import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.product.dto.request.ProductRequestDto;
import org.example.hansabal.domain.product.service.ProductService;
import org.example.hansabal.domain.review.dto.request.CreateReviewRequest;
import org.example.hansabal.domain.review.dto.request.UpdateReviewRequest;
import org.example.hansabal.domain.review.dto.response.CreateReviewResponse;
import org.example.hansabal.domain.review.dto.response.UpdateReviewResponse;
import org.example.hansabal.domain.review.entity.Review;
import org.example.hansabal.domain.review.repository.ReviewRepository;
import org.example.hansabal.domain.users.dto.request.UserCreateRequest;
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
@Sql(scripts = {"/review_test_db.sql", "/review_user_test_db.sql", "/review_product_test_db.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewRepository reviewRepository;

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
        Long productId = 2L;
        UserAuth userAuth = new UserAuth(1L, UserRole.USER,"testnickname1");
        CreateReviewRequest request = new CreateReviewRequest("test review", 5);

        //when
        CreateReviewResponse response = reviewService.createReview(productId, userAuth, request);

        //then
        Assertions.assertThat(response.getId().equals(1L));
        Assertions.assertThat(response.getNickname().equals("testnickname1"));
        Assertions.assertThat(response.getContent().equals("test review"));
        Assertions.assertThat(response.getRating().equals(5));
        Assertions.assertThat(response).isNotNull();
    }

    @Test
    void 리뷰_중복생성_예외() {
        //given
        Long productId = 1L;
        UserAuth userAuth = new UserAuth(1L, UserRole.USER,"testnickname1");
        CreateReviewRequest request = new CreateReviewRequest("test review", 5);

        //when, then
        assertThatThrownBy(() -> reviewService.createReview(productId, userAuth, request))
                .hasMessageContaining("해당 상품에 대한 리뷰를 이미 작성하셨습니다.");
    }

    @Test
    void 리뷰수정() {
        //given
        Long reviewId = 1L;
        UpdateReviewRequest updateReviewRequest = new UpdateReviewRequest("테스트 업데이트 리뷰", 4);
        UserAuth userAuth = new UserAuth(1L, UserRole.USER,"testnickname1");

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
        Long reviewId = 1L;
        UserAuth otherUserAuth = new UserAuth(2L, UserRole.USER,"testnickname1");

        UpdateReviewRequest updateReviewRequest = new UpdateReviewRequest("테스트 업데이트 리뷰", 4);

        //then,then
        assertThatThrownBy(() -> reviewService.updateReview(reviewId, updateReviewRequest, otherUserAuth))
                .hasMessageContaining("존재하지 않는 사용자입니다.");
    }

    @Test
    void 리뷰삭제() {
        //given
        Long reviewId = 1L;

        //when
        reviewService.deleteReview(reviewId);
        Optional<Review> review = reviewRepository.findById(reviewId);

        //then
        assertThat(review).isNotEmpty();
        assertThat(review.get().getDeletedAt()).isNotNull();
    }
}