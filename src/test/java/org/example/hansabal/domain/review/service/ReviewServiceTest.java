package org.example.hansabal.domain.review.service;

import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.review.dto.request.CreateReviewRequest;
import org.example.hansabal.domain.review.dto.response.CreateReviewResponse;
import org.example.hansabal.domain.review.repository.ReviewRepository;
import org.example.hansabal.domain.users.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
@Testcontainers
@ActiveProfiles("test")
class ReviewServiceTest {

    // Testcontainers 선언하면 테스트 하고 다시 롤백을 시킨다 ->> 그래서 mysql 컨테이너가 필요없다.
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    void 리뷰_생성() {
        //given
        Long productId = 1L;
        UserAuth userAuth = new UserAuth(1L, UserRole.USER);
        CreateReviewRequest request = new CreateReviewRequest("테스트 리뷰", 5);

        //when
        CreateReviewResponse response = reviewService.createReview(productId,userAuth,request);

        //then
        Assertions.assertThat(response.getContent().equals("테스트 리뷰"));
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isEqualTo(productId);
    }
}