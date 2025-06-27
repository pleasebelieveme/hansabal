package org.example.hansabal.domain.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hansabal.domain.review.dto.request.CreateReviewRequest;
import org.example.hansabal.domain.review.dto.request.UpdateReviewRequest;
import org.example.hansabal.domain.review.dto.response.CreateReviewResponse;
import org.example.hansabal.domain.review.dto.response.UpdateReviewResponse;
import org.example.hansabal.domain.review.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Testcontainers
@ActiveProfiles("test")
@Sql(scripts = {"/review_test_db.sql", "/review_product_test_db.sql", "/review_user_test_db.sql"}
        , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ReviewControllerTest {

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

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ReviewService reviewService;

    @Test
    @DisplayName("댓글 생성 성공 테스트")
    void createReviewTest() throws Exception {
        //given
        Long productId = 1L;
        CreateReviewResponse response = new CreateReviewResponse(1L, "testnickname1", "testreview", 5);
        CreateReviewRequest request = new CreateReviewRequest("testreview", 5);

        //when
        Mockito.when(reviewService.createReview(eq(productId), any(), any())).thenReturn(response);//productId가 해당 값일 때에 대해, 미리 준비한 응답 데이터(response)를 반환합니다.

        //then
        mockMvc.perform(post("/api/reviews/products/{productId}", productId)
                        .with(user("1").roles("USER"))
                        //사용자를 인증된 상태로 시뮬레이션하며,
                        // user("1"): 유저 ID가 "1"인 사용자
                        // roles("USER"): 사용자는 ROLE_USER 권한을 보유합니다.
                        .contentType(MediaType.APPLICATION_JSON) //요청 본문이 JSON 형식임을 나타냅니다.
                        .content(objectMapper.writeValueAsString(request))) //위에서 준비한 요청 데이터(request)를 JSON 문자열로 변환하여 본문에 추가합니다

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("testreview"));
    }

    @Test
    @DisplayName("리뷰 생성 실패 Valid 오류")
    void reviewDuplicationTest() throws Exception {
        //given
        Long productId = 1L;
        CreateReviewRequest request = new CreateReviewRequest("", 5);
        CreateReviewResponse response = new CreateReviewResponse(1L, "testnickname1", "", 5);

        //when
        Mockito.when(reviewService.createReview(eq(productId), any(), any())).thenReturn(response);

        //then
        mockMvc.perform(post("/api/reviews/products/{productId}", productId)
                        .with(user("1").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value("C001"))
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."));
    }

    @Test
    @DisplayName("리뷰 수정 성공 테스트")
    void updateReviewTest() throws Exception {
        //given
        Long reivewId = 1L;
        UpdateReviewResponse response = new UpdateReviewResponse(1L, "testnickname2", "testreview2", LocalDateTime.now(), 5);
        UpdateReviewRequest request = new UpdateReviewRequest("testreview2", 5);
        //when
        Mockito.when(reviewService.updateReview(eq(reivewId), any(), any())).thenReturn(response);
        //then
        mockMvc.perform(put("/api/reviews/{reivewId}", reivewId)
                        .with(user("1").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("testreview2"));
    }

    @Test
    @DisplayName("리뷰 삭제 성공 테스트")
    void deleteReviewTest() throws Exception {
        //given
        Long reivewId = 1L;
        CreateReviewResponse response = new CreateReviewResponse(1L, "testnickname1", "testreview", 5);
        CreateReviewRequest request = new CreateReviewRequest("testreview", 5);

        //when then
        mockMvc.perform(delete("/api/reviews/{reivewId}", reivewId)
                        .with(user("1").roles("USER")))
                .andExpect(status().isOk());

        verify(reviewService).deleteReview(reivewId); //reviewService라는 Mock 객체가 특정 메서드를 호출한 적이 있는지 확인합니다.
    }
}