package org.example.hansabal.domain.board.repository;

import org.example.hansabal.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // 카테고리별 조회 (페이징 없이 전체 조회)
    List<Board> findByCategory(String category);

    // 제목 또는 내용에 키워드가 포함된 게시글 조회 (간단한 검색)
    List<Board> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword);

    // 카테고리 + 제목/내용 검색 (복합 검색이 필요할 때)
    List<Board> findByCategoryAndTitleContainingOrCategoryAndContentContaining(
            String category1, String titleKeyword, String category2, String contentKeyword
    );
    // 카테고리별 페이징 조회
    Page<Board> findByCategory(String category, Pageable pageable);

    // 제목 또는 내용으로 키워드 검색 + 페이징
    Page<Board> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword, Pageable pageable);
}
