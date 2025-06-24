package org.example.hansabal.domain.board.repository;

import org.example.hansabal.domain.board.dto.response.BoardSimpleResponse;
import org.example.hansabal.domain.board.entity.Board;
import org.example.hansabal.domain.board.entity.BoardCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


public interface BoardRepositoryCustom {
    Page<BoardSimpleResponse> searchByCategoryAndKeyword(BoardCategory category,
                                                         String keyword,
                                                         Pageable pageable);

}
