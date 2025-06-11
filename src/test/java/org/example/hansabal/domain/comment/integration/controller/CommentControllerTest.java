package org.example.hansabal.domain.comment.integration.controller;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.comment.dto.request.CreateCommentRequest;
import org.example.hansabal.domain.comment.dto.response.CommentResponse;
import org.example.hansabal.domain.comment.service.CommentService;
import org.example.hansabal.domain.users.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Testcontainers
@ActiveProfiles("test")
@Sql(scripts = {"/user_test_db.sql","/board_test_db.sql"}
	,executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CommentControllerTest {
	/* 기존 WebMvcTest에서 SpringBootTest로 변경한 이유는
	*  Spring Security, Jwt, Redis 등등 인증인가에 엮여 있는 라이브러리들이 많아서
	*  Controller 단의 Bean만 호출하는 WebMvcTest는 어울리지 않다고 판단함(실제로 관련된 오류가 계속 터져서 어쩔 수 없었음)
	* */
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

	// 테스트 클래스에서 DI로 MockMvc를 주입받아 실제 HTTP 요청처럼 시뮬레이션 가능
	@Autowired
	MockMvc mockMvc;

	// JSON 직렬화/역직렬화를 위한 Jackson 라이브러리의 ObjectMapper 주입
	@Autowired
	ObjectMapper objectMapper;

	// @SpringBootTest 환경에서는 @MockBean이나 @MockitoBean으로 Service를 가짜로 주입 가능
	// @MockitoBean은 Spring 6.2+의 새로운 방식 (기존 @MockBean 대체)
	@MockitoBean
	CommentService commentService;

	@Test
	void creatComment() throws Exception{
		// given
		CreateCommentRequest request = new CreateCommentRequest("댓글");
		// 댓글 작성 후 반환될 응답 객체를 미리 준비 (Service가 반환한다고 가정함)
		CommentResponse response = new CommentResponse("댓글");

		/* commentService.createComment(...) 호출 시 response 를 리턴하도록 stubbing
		*  any() → 첫 번째 파라미터 (CreateCommentRequest)
		*  any() → 두 번째 파라미터 (UserAuth 또는 인증 객체)
		*  eq(1L) → boardId를 정확히 1L로 고정
		* */
		Mockito.when(commentService.createComment(any(),any(),eq(1L))).thenReturn(response);

		// when
		mockMvc.perform(post("/api/comments/boards/1") // POST 요청 (Spring MVC 내부에서 해당 URI로 매핑된 컨트롤러 메서드 호출)
			/* 인증된 사용자를 흉내내는 Mock 사용자 설정
			*  user("1") → principal이 "1"인 사용자 (userid가 "1"임)
			*  roles("USER") → ROLE_USER 권한을 부여함 (hasRole("USER")에 매칭)
			* */
				.with(user("1").roles("USER"))
				// HTTP 요청의 Content-Type 설정 (JSON 요청임을 명시)
				.contentType(MediaType.APPLICATION_JSON)
				// 요청 본문을 JSON 문자열로 변환하여 포함
				.content(objectMapper.writeValueAsString(request)))
			// then / HTTP 응답 상태 코드가 201 Created 인지 확인
			.andExpect(status().isCreated())
			// JSON 응답에서 "contents" 필드가 "댓글" 인지 확인
			.andExpect(jsonPath("$.contents").value("댓글"));
	}

}
