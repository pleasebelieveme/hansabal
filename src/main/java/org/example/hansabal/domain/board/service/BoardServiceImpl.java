package org.example.hansabal.domain.board.service;


import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.exception.CommonErrorCode;
import org.example.hansabal.domain.board.dto.BoardRequestDto;
import org.example.hansabal.domain.board.dto.BoardResponseDto;
import org.example.hansabal.domain.board.entity.Board;
import org.example.hansabal.domain.board.repository.BoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;

    @Override
    @Transactional
    public BoardResponseDto createPost(Long userId, BoardRequestDto dto) {
        Board board = Board.builder()
                .userId(userId)
                .category(dto.getCategory())
                .title(dto.getTitle())
                .content(dto.getContent())
                .viewCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Board saved = boardRepository.save(board);
        return toResponseDto(saved);
    }

    @Override
    @Transactional
    public BoardResponseDto updatePost(Long userId, Long postId, BoardRequestDto dto) {
        Board board = boardRepository.findById(postId)
                .orElseThrow(() -> new BizException(CommonErrorCode.POST_NOT_FOUND));
        if (!board.getUserId().equals(userId)) {
            throw new BizException(CommonErrorCode.FORBIDDEN);
        }
        board.update(dto.getCategory(), dto.getTitle(), dto.getContent());
        return toResponseDto(board);
    }

    @Override
    @Transactional
    public void deletePost(Long userId, Long postId) {
        Board board = boardRepository.findById(postId)
                .orElseThrow(() -> new BizException(CommonErrorCode.POST_NOT_FOUND));
        if (!board.getUserId().equals(userId)) {
            throw new BizException(CommonErrorCode.FORBIDDEN);
        }
        boardRepository.delete(board);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardResponseDto getPost(Long postId) {
        Board board = boardRepository.findById(postId)
                .orElseThrow(() -> new BizException(CommonErrorCode.POST_NOT_FOUND));
        return toResponseDto(board);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BoardResponseDto> getPosts(String category, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Board> boards = boardRepository.findByCategory(category, pageRequest);
        return boards.map(this::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BoardResponseDto> searchPosts(String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Board> boards = boardRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageRequest);
        return boards.map(this::toResponseDto);
    }

    private BoardResponseDto toResponseDto(Board board) {
        return BoardResponseDto.builder()
                .postId(board.getPostId())
                .userId(board.getUserId())
                .category(board.getCategory())
                .title(board.getTitle())
                .content(board.getContent())
                .viewCount(board.getViewCount())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }
}
