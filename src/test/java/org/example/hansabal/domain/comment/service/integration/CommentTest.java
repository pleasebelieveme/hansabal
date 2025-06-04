package org.example.hansabal.domain.comment.service.integration;

import static org.assertj.core.api.Assertions.*;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.comment.dto.request.CreateCommentRequest;
import org.example.hansabal.domain.comment.dto.response.CommentResponse;
import org.example.hansabal.domain.comment.repository.CommentRepository;
import org.example.hansabal.domain.comment.service.CommentService;
import org.example.hansabal.domain.review.service.ReviewServiceImpl;
import org.example.hansabal.domain.users.entity.UserRole;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Testcontainers
@Transactional
@ActiveProfiles("test")
@Sql(scripts = {"/user_test_db.sql","/board_test_db.sql","/comment_test_db.sql"}
	,executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Slf4j
public class CommentTest {

	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
		.withDatabaseName("testdb")
		.withUsername("testuser")
		.withPassword("testpass");

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private CommentService commentService;

	@Autowired
	private ReviewServiceImpl reviewService;

	@BeforeAll
	public static void beforeAll() {

	}

	@Test
	void 댓글_생성(){
		// given
		CreateCommentRequest request = new CreateCommentRequest("테스트 댓글");
		Long boardId = 1L;
		UserAuth userAuth = new UserAuth(1L, UserRole.USER);

		// when
		CommentResponse response = commentService.createComment(request, userAuth, boardId);

		// then
		// pr 확인용 주석
		assertThat(response).isNotNull();
		assertThat(response.contents().equals("테스트 댓글"));
	}

	@Test
	void 댓글_생성_유저정보_불일치_예외(){
		CreateCommentRequest request = new CreateCommentRequest("테스트 댓글");
		Long boardId = 1L;
		UserAuth userAuth = new UserAuth(2L, UserRole.USER);

		assertThatThrownBy( () -> {
			commentService.createComment(request, userAuth, boardId);
		}).isInstanceOf(BizException.class)
			.hasMessageContaining("유효하지 않은 id 입니다.");
	}

	@Test
	void 댓글_생성_보드정보_불일치_예외() {
		CreateCommentRequest request = new CreateCommentRequest("테스트 댓글");
		Long boardId = 2L;
		UserAuth userAuth = new UserAuth(1L, UserRole.USER);

		assertThatThrownBy( () -> {
			commentService.createComment(request, userAuth, boardId);
		}).isInstanceOf(BizException.class)
			.hasMessageContaining("유효하지 않은 id 입니다.");
	}

	@Test
	void 댓글_수정(){
		CreateCommentRequest request = new CreateCommentRequest("수정된 댓글");
		Long commentId = 1L;
		UserAuth userAuth = new UserAuth(1L, UserRole.USER);

		CommentResponse response = commentService.updateComment(request, commentId, userAuth);

		assertThat(response.contents().equals("수정된 댓글"));
	}


}
