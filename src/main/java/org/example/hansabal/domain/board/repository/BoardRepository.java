package org.example.hansabal.domain.board.repository;

import org.example.hansabal.domain.board.entity.Board;
import org.example.hansabal.domain.board.entity.BoardCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    // 카테고리별 조회
    Page<Board> findByCategory(BoardCategory category, Pageable pageable);

    // (2) 키워드로 전체 조회 (비권장/필요시만)
    List<Board> findByTitleContainingOrContentContaining(String title, String content);

    // (3) 복합 전체 검색 (비권장/필요시만)
    List<Board> findByCategoryAndTitleContainingOrCategoryAndContentContaining(
            String category1, String title, String category2, String content
    );

    // (4) 카테고리별 페이징
    Page<Board> findByCategory(String category, Pageable pageable);

    // (5) 키워드 검색 + 페이징
    Page<Board> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    // (6) 복합 조건 페이징 검색 (예시)
    Page<Board> findByCategoryAndTitleContainingOrCategoryAndContentContaining(
            String category1, String title, String category2, String content, Pageable pageable
    );
}
