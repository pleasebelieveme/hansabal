//package org.example.hansabal.domain.review.service;
//
//import jakarta.transaction.Transactional;
//import org.assertj.core.api.Assertions;
//import org.example.hansabal.common.jwt.UserAuth;
//import org.example.hansabal.domain.product.entity.Product;
//import org.example.hansabal.domain.product.entity.ProductStatus;
//import org.example.hansabal.domain.product.repository.ProductRepository;
//import org.example.hansabal.domain.review.dto.request.CreateReviewRequest;
//import org.example.hansabal.domain.review.dto.response.CreateReviewResponse;
//import org.example.hansabal.domain.review.repository.ReviewRepository;
//import org.example.hansabal.domain.users.entity.User;
//import org.example.hansabal.domain.users.entity.UserRole;
//import org.example.hansabal.domain.users.repository.UserRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.testcontainers.containers.MySQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//
//@SpringBootTest
//@Transactional
//@Testcontainers
//@ActiveProfiles("test")
//class ReviewServiceTest {
//
//    // Testcontainers 선언하면 테스트 하고 다시 롤백을 시킨다 ->> 그래서 mysql 컨테이너가 필요없다.
//    @Container
//    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
//            .withDatabaseName("testdb")
//            .withUsername("testuser")
//            .withPassword("testpass");
//
//    @Autowired
//    private ReviewService reviewService;
//
//    @Autowired
//    private ReviewRepository reviewRepository;
//
//    @Autowired
//    private UserRepository UserRepository;
//
//    @Autowired
//    private ProductRepository ProductRepository;
//
//    @Test
//    void 리뷰_생성() {
//        //given
//        User user = new User("테스트 이메일", "테스트 비밀번호", "테스트 이름", "테스트 닉네임");
//        UserAuth userAuth = new UserAuth(user.getId(), UserRole.USER);
//        Product product = new Product("테스트 제품", 1, ProductStatus.FOR_SALE);
//
//        CreateReviewRequest request = new CreateReviewRequest("테스트 리뷰", 5);
//
//        //when
//        CreateReviewResponse response = reviewService.createReview(product.getId(),userAuth,request);
//
//        //then
//        Assertions.assertThat(response.getContent().equals("테스트 리뷰"));
//        Assertions.assertThat(response).isNotNull();
//        Assertions.assertThat(response.getId()).isEqualTo(product.getId());
//    }
//}