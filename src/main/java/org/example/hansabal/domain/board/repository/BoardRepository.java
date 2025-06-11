package org.example.hansabal.domain.board.repository;

import org.example.hansabal.domain.board.entity.Board;
import org.example.hansabal.domain.board.entity.BoardCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 카테고리별 조회
    Page<Board> findByCategory(BoardCategory category, Pageable pageable);

    // 제목 또는 내용에 키워드가 포함된 글 검색
    Page<Board> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    // 카테고리 + 키워드 동시 검색
    @Query("SELECT b FROM Board b WHERE b.category = :category AND (b.title LIKE %:keyword% OR b.content LIKE %:keyword%)")
    Page<Board> searchByCategoryAndKeyword(@Param("category") BoardCategory category,
                                           @Param("keyword") String keyword,
                                           Pageable pageable);
}
