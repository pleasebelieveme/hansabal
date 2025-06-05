package org.example.hansabal.domain.comment.service;

import java.util.Optional;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.board.entity.Board;
import org.example.hansabal.domain.board.repository.BoardRepository;
import org.example.hansabal.domain.comment.dto.request.DibRequest;
import org.example.hansabal.domain.comment.entity.Comment;
import org.example.hansabal.domain.comment.entity.Dib;
import org.example.hansabal.domain.comment.entity.DibType;
import org.example.hansabal.domain.comment.exception.DibErrorCode;
import org.example.hansabal.domain.comment.repository.CommentRepository;
import org.example.hansabal.domain.comment.repository.DibRepository;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DibService {

	private final DibRepository dibRepository;
	private final UserRepository userRepository;
	private final CommentRepository commentRepository;
	private final BoardRepository boardRepository;

	public void modifyDibs(Long userId, DibRequest request) {
		User user = userRepository.findById(userId).orElseThrow(
			() -> new BizException(DibErrorCode.INVALID_ID));

		Optional<Dib> existing = dibRepository.findByUserAndDibTypeAndTargetId(user, request.dibType(),
			request.targetId());

		// 좋아요 취소 메서드
		if(existing.isPresent()) {

			dibRepository.delete(existing.get());

			switch (request.dibType()) {

				case COMMENT -> commentRepository.findById(request.targetId())
					.ifPresent(Comment::decreaseDibs);

				case BOARD -> boardRepository.findById(request.targetId())
					.ifPresent(Board::decreaseDibs);
			}

			return;
		}

		// 좋아요 추가 메서드
		dibRepository.save(new Dib(request.dibType(), request.targetId(), user));

		switch (request.dibType()) {
			case COMMENT -> commentRepository.findById(request.targetId())
				.ifPresent(Comment::increaseDibs);
			case BOARD -> boardRepository.findById(request.targetId())
				.ifPresent(Board::increaseDibs);
		}
	}

}
