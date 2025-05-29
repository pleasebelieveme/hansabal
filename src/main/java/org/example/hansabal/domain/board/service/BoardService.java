package org.example.hansabal.domain.board.service;


import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.board.dto.response.BoardRequest;
import org.example.hansabal.domain.board.dto.request.BoardResponse;
import org.example.hansabal.domain.board.entity.Board;
import org.example.hansabal.domain.board.exception.BoardErrorCode;
import org.example.hansabal.domain.board.repository.BoardRepository;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    @Transactional
    public BoardResponse createPost(User user, BoardRequest request) {
        Board board = Board.builder()
                .user(user)
                .category(request.getCategory())
                .title(request.getTitle())
                .content(request.getContent())
                .viewCount(0)
                .build();
        Board saved = boardRepository.save(board);
        return toResponse(saved);
    }

    @Transactional
    public BoardResponse updatePost(User user, Long postId, BoardRequest request) {
        Board board = boardRepository.findById(postId)
                .orElseThrow(() -> new BizException(BoardErrorCode.POST_NOT_FOUND));
        if (!board.getUser().getId().equals(user.getId())) {
            throw new BizException(BoardErrorCode.FORBIDDEN);
        }
        board.update(request.getCategory(), request.getTitle(), request.getContent());
        return toResponse(board);
    }

    @Transactional
    public void deletePost(User user, Long postId) {
        Board board = boardRepository.findById(postId)
                .orElseThrow(() -> new BizException(BoardErrorCode.POST_NOT_FOUND));
        if (!board.getUser().getId().equals(user.getId())) {
            throw new BizException(BoardErrorCode.FORBIDDEN);
        }
        boardRepository.delete(board);
    }

    @Transactional(readOnly = true)
    public BoardResponse getPost(Long postId) {
        Board board = boardRepository.findById(postId)
                .orElseThrow(() -> new BizException(BoardErrorCode.POST_NOT_FOUND));
        return toResponse(board);
    }

    @Transactional(readOnly = true)
    public Page<BoardResponse> getPosts(String category, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Board> boards = boardRepository.findByCategory(category, pageRequest);
        return boards.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<BoardResponse> searchPosts(String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Board> boards = boardRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageRequest);
        return boards.map(this::toResponse);
    }

    // 작성자 정보 포함 변환
    private BoardResponse toResponse(Board board) {
        User user = board.getUser();
        return BoardResponse.builder()
                .postId(board.getPostId())
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .category(board.getCategory())
                .title(board.getTitle())
                .content(board.getContent())
                .viewCount(board.getViewCount())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }
}