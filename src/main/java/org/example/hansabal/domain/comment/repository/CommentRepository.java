package org.example.hansabal.domain.comment.repository;

import java.util.List;

import org.example.hansabal.domain.comment.dto.response.CommentResponse;
import org.example.hansabal.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {

	@Query("""
		SELECT c From Comment c 
		WHERE c.board.postId = :boardId 
		AND c.deletedAt IS NULL
	""")
	Page<Comment> findByBoardId(Long boardId, Pageable pageable); // 추후에 쿼리 DSL로 리팩토링 및 고도화 작업 예정
}
