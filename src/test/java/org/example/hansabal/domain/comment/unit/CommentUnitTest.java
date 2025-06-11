package org.example.hansabal.domain.comment.unit;


import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.board.entity.Board;
import org.example.hansabal.domain.board.entity.BoardCategory;
import org.example.hansabal.domain.board.repository.BoardRepository;
import org.example.hansabal.domain.comment.dto.request.CreateCommentRequest;
import org.example.hansabal.domain.comment.dto.response.CommentResponse;
import org.example.hansabal.domain.comment.entity.Comment;
import org.example.hansabal.domain.comment.repository.CommentRepository;
import org.example.hansabal.domain.comment.service.CommentService;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class CommentUnitTest {

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private BoardRepository boardRepository;

	@InjectMocks
	private CommentService commentService;

	private User user;
	private Board board;

	@BeforeEach
	void setUp() {
		user = new User(1L,"test@email.com","!Aa123456","testname","testnickname",UserRole.USER);
		board = Board.builder()
			.id(1L)
			.title("testtitle")
			.content("testcontent")
			.dibCount(0)
			.viewCount(0)
			.category(BoardCategory.ALL)
			.user(user)
			.build();
	}

	@Test
	void 댓글_생성_성공() {
		// given
		CreateCommentRequest request = new CreateCommentRequest("테스트 댓글");
		given(userRepository.findById(1L)).willReturn(Optional.of(user));
		given(boardRepository.findById(1L)).willReturn(Optional.of(board));

		Comment mockComment = new Comment("테스트 댓글", user, board);
		given(commentRepository.save(any())).willReturn(mockComment);

		// when
		CommentResponse response = commentService.createComment(request, new UserAuth(1L, UserRole.USER), 1L);

		// then
		assertThat(response).isNotNull();
		assertThat(response.contents()).isEqualTo("테스트 댓글");
	}

	@Test
	void 댓글_생성_유저없음_예외() {
		// given
		CreateCommentRequest request = new CreateCommentRequest("테스트 댓글");
		given(userRepository.findById(2L)).willReturn(Optional.empty()); // DB에 1L인데 요청은 2L
		UserAuth userAuth = new UserAuth(2L,UserRole.USER);

		// when & then
		assertThatThrownBy(() -> {
			commentService.createComment(request,userAuth,1L);
		}).isInstanceOf(BizException.class)
			.hasMessageContaining("유효하지 않은 id 입니다.");
	}

	@Test
	void 댓글_삭제() {
		// given
		Comment comment = new Comment("댓글 내용", user, board);
		ReflectionTestUtils.setField(comment, "id", 1L); // id 세팅

		given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

		// when
		commentService.deleteComment(1L);

		// then
		assertThat(comment.getDeletedAt()).isNotNull();
	}

	@Test
	void 댓글_수정(){
		// given
		Comment comment = new Comment("댓글",user,board);
		ReflectionTestUtils.setField(comment,"id",1L);
		CreateCommentRequest request = new CreateCommentRequest("수정된 댓글");

		given(commentRepository.findById(1L)).willReturn(Optional.of(comment));
		UserAuth userAuth = new UserAuth(1L,UserRole.USER);

		// when
		CommentResponse response = commentService.updateComment(request, 1L, userAuth);

		// then
		assertThat(response.contents().equals("수정된 댓글"));
	}

	@Test
	void 댓글_수정_권한없음_예외(){
		// given
		Comment comment = new Comment("댓글",user,board);
		ReflectionTestUtils.setField(comment,"id",1L);
		CreateCommentRequest request = new CreateCommentRequest("수정된 댓글");

		given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

		UserAuth userAuth = new UserAuth(2L,UserRole.USER);

		assertThatThrownBy( () -> {
			commentService.updateComment(request,1L,userAuth);
		}).isInstanceOf(BizException.class)
			.hasMessageContaining("해당 권한이 없습니다.");
	}

	@Test
	void 댓글_조회() {
		// 테스트 불가능
	}
}
