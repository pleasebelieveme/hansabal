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
import org.example.hansabal.domain.comment.dto.response.CommentPageResponse;
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

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final CommentService commentService;
    private final DibService dibService;
    private final CommentRepository commentRepository;
    private final BoardMapper boardMapper;


    // === 게시글 등록 ===
    @Transactional
    public BoardResponse createPost(UserAuth userAuth, BoardRequest request) {
        User user = userRepository.findByIdOrElseThrow(userAuth.getId());
        Board board = Board.builder()
                .user(user)
                .category(BoardCategory.fromDisplayName(request.getCategory())) // ✅ 변경
                .title(request.getTitle())
                .content(request.getContent())
                .dibCount(0)
                .viewCount(0)
                .build();
        Board saved = boardRepository.save(board);
        return boardMapper.toResponse(saved);
    }

    // === 게시글 수정 ===
    @Transactional
    public BoardResponse updatePost(UserAuth userAuth, Long Id, BoardRequest request) {
        User user = userRepository.findByIdOrElseThrow(userAuth.getId());
        Board board = boardRepository.findById(Id)
                .orElseThrow(() -> new BizException(BoardErrorCode.POST_NOT_FOUND));
        if (!board.getUser().getId().equals(user.getId())) {
            throw new BizException(BoardErrorCode.FORBIDDEN);
        }

        // 카테고리 + 제목 + 내용 업데이트
        board.update(BoardCategory.valueOf(request.getCategory().toUpperCase()), request.getTitle(), request.getContent());
        return boardMapper.toResponse(board);
    }
    // === 게시글 삭제 ===
    @Transactional
    public void deletePost(UserAuth userAuth, Long Id) {
        User user = userRepository.findByIdOrElseThrow(userAuth.getId());
        Board board = boardRepository.findById(Id)
                .orElseThrow(() -> new BizException(BoardErrorCode.POST_NOT_FOUND));
        if (!board.getUser().getId().equals(user.getId())) {
            throw new BizException(BoardErrorCode.FORBIDDEN);
        }
        boardRepository.delete(board);
    }

    // === 게시글 상세 조회 ===
    @Transactional(readOnly = true)
    public BoardResponse getPost(Long postId, Pageable pageable) {
        // 1. 게시글 엔티티 조회
        Board board = boardRepository.findById(postId)
                .orElseThrow(() -> new BizException(BoardErrorCode.POST_NOT_FOUND));



        // 2. 좋아요(찜) 개수 - Board 엔티티의 필드값 사용
        int likeCount = board.getDibCount();

        // 3. 댓글 리스트 제거에 따라 빈 리스트 전달 또는 null 처리 (선택)
        List<CommentPageResponse> comments = Collections.emptyList(); // 또는 null

        // 4. 응답 조립 및 반환
        return boardMapper.toResponse(board, comments, likeCount, false);
    }

    // === 게시글 목록 조회 (카테고리 + 키워드 포함) ===
    @Transactional(readOnly = true)
    public Page<BoardResponse> getPosts(String category, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Board> boardPage;

        boolean isAll = category.equalsIgnoreCase("ALL");
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        if (isAll && hasKeyword) {
            // 1. 전체 + 검색어 → 제목/내용에 키워드 포함된 게시글
            boardPage = boardRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);

        } else if (!isAll && hasKeyword) {
            // 2. 특정 카테고리 + 검색어
            BoardCategory boardCategory = BoardCategory.fromDisplayName(category);
            boardPage = boardRepository.searchByCategoryAndKeyword(boardCategory, keyword, pageable);

        } else if (!isAll) {
            // 3. 특정 카테고리만
            BoardCategory boardCategory = BoardCategory.fromDisplayName(category);
            boardPage = boardRepository.findByCategory(boardCategory, pageable);

        } else {
            // 4. 전체 조회
            boardPage = boardRepository.findAll(pageable);
        }

        return boardPage.map(boardMapper::toResponse);
    }
}