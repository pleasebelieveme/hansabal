package org.example.hansabal.domain.comment.service;


import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.board.entity.Board;
import org.example.hansabal.domain.board.repository.BoardRepository;
import org.example.hansabal.domain.comment.dto.request.CreateCommentRequest;
import org.example.hansabal.domain.comment.dto.response.CommentResponse;
import org.example.hansabal.domain.comment.entity.Comment;
import org.example.hansabal.domain.comment.exception.CommentErrorCode;
import org.example.hansabal.domain.comment.repository.CommentRepository;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final UserRepository userRepository;
	private final BoardRepository boardRepository;

	@Transactional
	public CommentResponse createComment(CreateCommentRequest request,Long userId,Long boardId) {

		User user = userRepository.findById(userId).orElseThrow(
			() -> new BizException(CommentErrorCode.INVALID_ID));

		Board board = boardRepository.findById(boardId).orElseThrow(
			() -> new BizException(CommentErrorCode.INVALID_ID));

		Comment comment = new Comment(request.contents(),user,board);

		commentRepository.save(comment);

		return CommentResponse.from(comment);
	}

	@Transactional
	public CommentResponse updateComment(CreateCommentRequest request, Long commentId) {

		Comment comment = commentRepository.findById(commentId).orElseThrow(
			() -> new BizException(CommentErrorCode.INVALID_ID));

		// if (!comment.getUser().getId().equals(user.getId())) {
		// 	throw new BizException(CommentErrorCode.FORBIDDEN);
		// }

		comment.updateContents(request.contents());

		return CommentResponse.from(comment);
	}
  
	@Transactional(readOnly = true)
	public Page<CommentResponse> findAllCommentsFromBoard(Long boardId, int page, int size) {
		int pageIndex = Math.max(page - 1 , 0);
		Pageable pageable = PageRequest.of(pageIndex,size);
		// 추후에 쿼리 DSL로 리팩토링 및 고도화 작업 예정
		Page<Comment> comments = commentRepository.findByBoardId(boardId, pageable);

		return comments.map(CommentResponse::from);
	}

	@Transactional
	public void deleteComment(Long commentId) {
		Comment comment = commentRepository.findById(commentId).orElseThrow(
			() -> new BizException(CommentErrorCode.INVALID_ID));

		comment.softDelete();
	}
}
