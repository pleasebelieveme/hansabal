package org.example.hansabal.domain.board.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.board.dto.request.BoardRequest;
import org.example.hansabal.domain.board.dto.response.BoardPageResponse;
import org.example.hansabal.domain.board.dto.response.BoardPageResult;
import org.example.hansabal.domain.board.dto.response.BoardResponse;
import org.example.hansabal.domain.board.dto.response.BoardSimpleResponse;
import org.example.hansabal.domain.board.entity.Board;
import org.example.hansabal.domain.board.entity.BoardCategory;
import org.example.hansabal.domain.board.exception.BoardErrorCode;
import org.example.hansabal.domain.board.repository.BoardRepository;
import org.example.hansabal.domain.comment.dto.response.CommentPageResponse;
import org.example.hansabal.domain.comment.repository.CommentRepository;
import org.example.hansabal.domain.comment.service.CommentService;
import org.example.hansabal.domain.comment.service.DibService;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final CommentService commentService;
    private final DibService dibService;
    private final CommentRepository commentRepository;
    private final BoardMapper boardMapper;
    private final BoardServiceUtill boardServiceUtill;


    // === 게시글 등록 ===
    @Transactional
    public BoardResponse createBoard(UserAuth userAuth, BoardRequest request) {
        if(request.getCategory().equals(BoardCategory.ALL)){
            throw new BizException(BoardErrorCode.INVALID_CATEGORY);
        }
        User user = userRepository.findByIdOrElseThrow(userAuth.getId());

        log.info("🔥 BoardService.createPost() 진입");
        log.info("작성자 ID: {}", userAuth.getId());
        log.info("제목: {}", request.getTitle());
        log.info("카테고리: {}", request.getCategory());

        Board board = Board.builder()
                .user(user)
                .category(request.getCategory()) // ✅ 변경
                .title(request.getTitle())
                .content(request.getContent())
                .dibCount(0)
                .viewCount(0)
                .build();
        Board saved = boardRepository.save(board);

        // 4. 저장 결과 확인
        log.info("✅ 저장된 글 ID: {}", saved.getId());
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
        board.update(request.getCategory(), request.getTitle(), request.getContent());
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
    public BoardResponse getPost(Long postId) {

        boardServiceUtill.viewCount(postId);
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

    @Cacheable(
            value = "BoardPostsCache",
            key = "#category + ':' + #keyword + ':' + #page + ':' + #size",
            unless = "#result == null || #result.isEmpty()"
    )
    // === 게시글 목록 조회 (카테고리 + 키워드 포함) ===
    @Transactional(readOnly = true)
    public BoardPageResult getPosts(BoardCategory category, String keyword, int page, int size) {
       int pageIndex = Math.max(page - 1,0);
        Pageable pageable = PageRequest.of(pageIndex, size);
        Page<BoardSimpleResponse> pageResult = boardRepository.searchByCategoryAndKeyword(category, keyword,
            pageable);

        return BoardPageResult.of(pageResult);
    }
}