package org.example.hansabal.domain.comment.integration.controller;


import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.List;

import org.example.hansabal.domain.comment.dto.request.CreateCommentRequest;
import org.example.hansabal.domain.comment.dto.response.CommentPageResponse;
import org.example.hansabal.domain.comment.dto.response.CommentResponse;
import org.example.hansabal.domain.comment.entity.Comment;
import org.example.hansabal.domain.comment.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Testcontainers
@ActiveProfiles("test")
@Sql(scripts = {"/comment_user_test_db.sql", "/comment_board_test_db.sql","/comment_test_db.sql"}
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
	@DisplayName("댓글 생성 성공 테스트")
	void createCommentTest() throws Exception{
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

	@Test
	@DisplayName("댓글 생성 실패 Valid 오류")
	void createComment_vaild_exception() throws Exception{
		// given
		CreateCommentRequest request = new CreateCommentRequest("");

		// when & then
		mockMvc.perform(post("/api/comments/boards/1")
			.with(user("1").roles("USER"))
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value("C001"))
			.andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."))
			.andExpect(jsonPath("$.errors[0].field").value("contents"))
			.andExpect(jsonPath("$.errors[0].rejectedValue").value(""));
	}

	@Test
	@DisplayName("댓글 수정 테스트")
	void updateCommentTest() throws Exception {
		// given
		Long commentId = 1L;
		CreateCommentRequest request = new CreateCommentRequest("수정된 댓글");
		CommentResponse response = new CommentResponse("수정된 댓글");
		// refEq는 equals의 정의가 되어있지 않을때 사용하는데 record 타입이라면 자동으로 정의 되어 있기 때문에 그냥 eq를 사용해도 된다.
		Mockito.when(commentService.updateComment(eq(request),eq(1L),any())).thenReturn(response);

		// when & then
		mockMvc.perform(patch("/api/comments/{commentId}",commentId)
			.with(user("1").roles("USER"))
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.contents").value("수정된 댓글"));
	}

	@Test
	@DisplayName("댓글 삭제 테스트")
	void deleteCommentTest() throws Exception {
		// given
		Long commentId = 1L;

		// when & then
		mockMvc.perform(delete("/api/comments/{commentId}",commentId)
			.with(user("1").roles("USER")))
			.andExpect(status().isOk());

		verify(commentService).deleteComment(commentId);
	}

	@Test
	@DisplayName("댓글 목록 1페이지 요청 테스트")
	void findAllCommentsFromBoardTestV1() throws Exception{
		// given
		List<CommentPageResponse> contents = List.of(
			new CommentPageResponse("댓글1", 3),
			new CommentPageResponse("댓글2", 5)
		);

		Page<CommentPageResponse> responsePage = new PageImpl<>(
			contents,PageRequest.of(0, 100),201);

		Mockito.when(commentService.findAllCommentsFromBoard(eq(1L), eq(1), eq(100)))
			.thenReturn(responsePage);

		// when & then
		mockMvc.perform(get("/api/comments/{boardId}", 1L)
				.param("page", "1")
				.param("size", "100")
				.with(user("1").roles("USER")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content.length()").value(2))
			.andExpect(jsonPath("$.content[0].comments").value("댓글1"))
			.andExpect(jsonPath("$.content[1].dibCount").value(5));
	}

}
