package org.example.hansabal.domain.comment.repository;

import org.example.hansabal.domain.comment.dto.response.CommentPageResponse;
import org.example.hansabal.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {

	Page<CommentPageResponse> findByBoardId(Long boardId, Pageable pageable);
}
