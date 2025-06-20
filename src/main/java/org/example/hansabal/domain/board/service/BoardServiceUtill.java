package org.example.hansabal.domain.board.service;


import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.redisson.DistributedLock;
import org.example.hansabal.domain.board.dto.response.BoardResponse;
import org.example.hansabal.domain.board.entity.Board;
import org.example.hansabal.domain.board.exception.BoardErrorCode;
import org.example.hansabal.domain.board.repository.BoardRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoardServiceUtill {
    private final BoardRepository boardRepository;

    @DistributedLock(key = "'DIB:BOARD:' + #postId")
    public void viewCount (Long postId) {
        Board board = boardRepository.findById(postId)
                .orElseThrow(() -> new BizException(BoardErrorCode.BOARD_NOT_FOUND));
        board.increaseViewCount();
    }
}
