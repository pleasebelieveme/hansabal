package org.example.hansabal.domain.board.service;

import org.example.hansabal.domain.board.dto.BoardRequestDto;
import org.example.hansabal.domain.board.dto.BoardResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BoardService {
    BoardResponseDto createPost(Long userId, BoardRequestDto dto);
    BoardResponseDto updatePost(Long userId, Long postId, BoardRequestDto dto);
    void deletePost(Long userId, Long postId);
    BoardResponseDto getPost(Long postId);
    Page<BoardResponseDto> getPosts(String category, int page, int size);
    Page<BoardResponseDto> searchPosts(String keyword, int page, int size);
}
