package org.example.hansabal.domain.board.service;


import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.board.dto.request.BoardRequest;
import org.example.hansabal.domain.board.dto.response.BoardResponse;
import org.example.hansabal.domain.board.entity.Board;
import org.example.hansabal.domain.board.entity.BoardCategory;
import org.example.hansabal.domain.board.exception.BoardErrorCode;
import org.example.hansabal.domain.board.repository.BoardRepository;
import org.example.hansabal.domain.comment.dto.response.CommentResponse;
import org.example.hansabal.domain.comment.entity.Comment;
import org.example.hansabal.domain.comment.repository.CommentRepository;
import org.example.hansabal.domain.comment.service.CommentService;
import org.example.hansabal.domain.comment.service.DibService;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final CommentService commentService;
    private final DibService dibService;
    private final CommentRepository commentRepository;

    @Transactional
    public BoardResponse createPost(UserAuth userAuth, BoardRequest request) {
        User user = userRepository.findByIdOrElseThrow(userAuth.getId());
        Board board = Board.builder()
                .user(user)
                .category(BoardCategory.valueOf(request.getCategory().toUpperCase())) // 반드시 enum으로 변환
                .title(request.getTitle())
                .content(request.getContent())
                .viewCount(0)
                .build();
        Board saved = boardRepository.save(board);
        return toResponse(saved);
    }

    @Transactional
    public BoardResponse updatePost(UserAuth userAuth, Long postId, BoardRequest request) {
        User user = userRepository.findByIdOrElseThrow(userAuth.getId());
        Board board = boardRepository.findById(postId)
                .orElseThrow(() -> new BizException(BoardErrorCode.POST_NOT_FOUND));
        if (!board.getUser().getId().equals(user.getId())) {
            throw new BizException(BoardErrorCode.FORBIDDEN);
        }
        board.update(BoardCategory.valueOf(request.getCategory().toUpperCase()), request.getTitle(), request.getContent());
        return toResponse(board);
    }

    @Transactional
    public void deletePost(UserAuth userAuth, Long postId) {
        User user = userRepository.findByIdOrElseThrow(userAuth.getId());
        Board board = boardRepository.findById(postId)
                .orElseThrow(() -> new BizException(BoardErrorCode.POST_NOT_FOUND));
        if (!board.getUser().getId().equals(user.getId())) {
            throw new BizException(BoardErrorCode.FORBIDDEN);
        }
        boardRepository.delete(board);
    }

    @Transactional(readOnly = true)
    public BoardResponse getPost(Long postId, Pageable pageable) {
        // 1. 게시글 엔티티 조회
        Board board = boardRepository.findById(postId)
                .orElseThrow(() -> new BizException(BoardErrorCode.POST_NOT_FOUND));

        // 2. 댓글 리스트 (페이징 적용)
        Page<Comment> commentPage = commentRepository.findByBoardId(postId, pageable);
        List<CommentResponse> comments = commentPage
                .stream()
                .map(CommentResponse::from)
                .toList();

        // 3. 좋아요(찜) 개수 - Board 엔티티의 필드값 사용
        int likeCount = board.getDibCount();


        // 5. 응답 조립 및 반환
        return BoardResponse.from(board, comments, likeCount, false /* likedByMe: 구현 시 교체 */);
    }

    @Transactional(readOnly = true)
    public Page<BoardResponse> getPosts(BoardCategory category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Board> boardPage;
        if (category == BoardCategory.ALL) {
            boardPage = boardRepository.findAll(pageable);
        } else {
            boardPage = boardRepository.findByCategory(category, pageable);
        }
        // Board → BoardResponse 변환 예시
        return boardPage.map(BoardResponse::from);
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
                .postId(board.getId())
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .category(board.getCategory().getDisplayName()) // ★ 여기만 getDisplayName()으로 수정
                .title(board.getTitle())
                .content(board.getContent())
                .viewCount(board.getViewCount())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }
}