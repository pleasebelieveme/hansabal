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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

		Comment comment = new Comment(request.content(),user,board);

		commentRepository.save(comment);

		return CommentResponse.from(comment);
	}
}
