package org.example.hansabal.domain.comment.service;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.redisson.DistributedLock;
import org.example.hansabal.domain.board.entity.Board;
import org.example.hansabal.domain.board.repository.BoardRepository;
import org.example.hansabal.domain.comment.entity.Comment;
import org.example.hansabal.domain.comment.entity.Dib;
import org.example.hansabal.domain.comment.entity.DibType;
import org.example.hansabal.domain.comment.exception.DibErrorCode;
import org.example.hansabal.domain.comment.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DibServiceUtil {
	private final CommentRepository commentRepository;
	private final BoardRepository boardRepository;
	@Autowired
	private final RedisTemplate<String, Object> redisTemplate;


	@DistributedLock(key = "'DIB:' + #dibType.name() + ':' + #targetId")
	public void dibMethod(DibType dibType,Long targetId, boolean flag){
		switch (dibType) {
			case COMMENT -> {
				Comment comment = commentRepository.findById(targetId)
					.orElseThrow(() -> new BizException(DibErrorCode.INVALID_ID));

				if(flag){
					comment.decreaseDibs();
				} else {
					comment.increaseDibs();
				}
				redisTemplate.delete("commentList::" + targetId);
			}

			case BOARD -> {
				Board board = boardRepository.findById(targetId)
				.orElseThrow(() -> new BizException(DibErrorCode.INVALID_ID));

				if(flag){
					board.decreaseDibs();
				} else {
					board.increaseDibs();
				}
				redisTemplate.delete("boardList::" + targetId);
			}
		}
	}
}
