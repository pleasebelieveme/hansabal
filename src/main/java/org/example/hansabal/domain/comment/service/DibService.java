package org.example.hansabal.domain.comment.service;

import java.util.Optional;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.redisson.DistributedLock;
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
	private final DibServiceUtil util;


	public void modifyDibs(Long userId, DibRequest request) {
		User user = userRepository.findById(userId).orElseThrow(
			() -> new BizException(DibErrorCode.INVALID_ID));

		Optional<Dib> existing = dibRepository.findByUserAndDibTypeAndTargetId(user, request.dibType(),
			request.targetId());


		if(existing.isPresent()) {
			dibRepository.delete(existing.get());
		} else {
			dibRepository.save(new Dib(request.dibType(), request.targetId(), user));
		}

		util.dibMethod(request.dibType(),request.targetId(), existing.isPresent());
	}

}
