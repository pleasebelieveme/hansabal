package org.example.hansabal.domain.comment.integration.service;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.comment.dto.request.CreateCommentRequest;
import org.example.hansabal.domain.comment.dto.response.CommentPageResponse;
import org.example.hansabal.domain.comment.dto.response.CommentPageResult;
import org.example.hansabal.domain.comment.dto.response.CommentResponse;
import org.example.hansabal.domain.comment.entity.Comment;
import org.example.hansabal.domain.comment.repository.CommentRepository;
import org.example.hansabal.domain.comment.service.CommentService;
import org.example.hansabal.domain.users.entity.UserRole;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Testcontainers
@Transactional
@ActiveProfiles("test")
@Sql(scripts = {"classpath:comment_user_test_db.sql", "classpath:comment_board_test_db.sql", "classpath:comment_test_db.sql"}
	,executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommentServiceTest {

	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
		.withDatabaseName("testdb")
		.withUsername("testuser")
		.withPassword("testpass");

	@Container
	static GenericContainer<?> redis = new GenericContainer<>("redis:6.2")
		.withExposedPorts(6379);

	@BeforeAll
	public void beforeAll() {
		System.setProperty("spring.data.redis.host", redis.getHost());
		System.setProperty("spring.data.redis.port", redis.getMappedPort(6379).toString());
	}

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private CommentService commentService;

	@Test
	void 댓글_생성(){
		// given
		CreateCommentRequest request = new CreateCommentRequest("테스트 댓글");
		Long boardId = 1L;
		UserAuth userAuth = new UserAuth(1L, UserRole.USER,"testnickname1");

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
		UserAuth userAuth = new UserAuth(2L, UserRole.USER,"testnickname1");

		assertThatThrownBy( () -> {
			commentService.createComment(request, userAuth, boardId);
		}).isInstanceOf(BizException.class)
			.hasMessageContaining("유효하지 않은 id 입니다.");
	}

	@Test
	void 댓글_생성_보드정보_불일치_예외() {
		CreateCommentRequest request = new CreateCommentRequest("테스트 댓글");
		Long boardId = 3L;
		UserAuth userAuth = new UserAuth(1L, UserRole.USER,"testnickname1");

		assertThatThrownBy( () -> {
			commentService.createComment(request, userAuth, boardId);
		}).isInstanceOf(BizException.class)
			.hasMessageContaining("유효하지 않은 id 입니다.");
	}

	@Test
	@DisplayName("여기다가 직관적이게 적기!!")
	void 댓글_수정(){
		CreateCommentRequest request = new CreateCommentRequest("수정된 댓글");
		Long commentId = 1L;
		UserAuth userAuth = new UserAuth(1L, UserRole.USER,"testnickname1");

		CommentResponse response = commentService.updateComment(request, commentId, userAuth);

		assertThat(response.contents().equals("수정된 댓글"));
	}

	@Test
	void 댓글_수정_권한없음_예외(){
		CreateCommentRequest request = new CreateCommentRequest("수정된 댓글");
		Long commentId = 1L;
		UserAuth userAuth = new UserAuth(2L, UserRole.USER,"testnickname1");

		assertThatThrownBy( () -> {
			commentService.updateComment(request, commentId, userAuth);
		}).isInstanceOf(BizException.class)
			.hasMessageContaining("해당 권한이 없습니다.");
	}

	@Test
	void 댓글_삭제(){
		Long commentId = 1L;

		commentService.deleteComment(commentId);

		Optional<Comment> comment = commentRepository.findById(commentId);

		assertThat(comment).isNotEmpty();
		assertThat(comment.get().getDeletedAt()).isNotNull();
	}

	@Test
	void 댓글_조회(){
		CommentPageResult result = commentService.findAllCommentsFromBoard(1L, 1, 100);

		assertThat(result.contents()).hasSize(100);
		assertThat(result.total()).isEqualTo(201);
		assertThat(result.contents().get(0).getComments()).contains("test comment");
		assertThat(result.contents().get(99).getDibCount()).isEqualTo(0);
	}

}
