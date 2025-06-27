package org.example.hansabal.domain.board.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.board.dto.request.BoardRequest;
import org.example.hansabal.domain.board.dto.response.BoardPageResponse;
import org.example.hansabal.domain.board.dto.response.BoardResponse;
import org.example.hansabal.domain.board.dto.response.BoardSimpleResponse;
import org.example.hansabal.domain.board.entity.BoardCategory;
import org.example.hansabal.domain.board.service.BoardService;
import org.example.hansabal.domain.board.service.BoardServiceUtill;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final BoardServiceUtill boardServiceUtill;


    // 게시글 등록
    @PostMapping
    public ResponseEntity<BoardResponse> createPost(
            @RequestBody @Valid BoardRequest request,
            @AuthenticationPrincipal UserAuth userAuth
    ) {
        log.info("📨 writePost 진입!");
        log.info("title = {}, content = {}, category = {}", request.getTitle(), request.getContent(), request.getCategory());

        System.out.println("✅ createPost 도착함");
        System.out.println("📦 서비스 호출 직전: " + request);
        BoardResponse response = boardService.createBoard(userAuth, request);
        System.out.println("📦 응답 객체: " + response);
        return ResponseEntity.status(201).body(response);
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<BoardResponse> updatePost(
            @PathVariable Long postId,
            @RequestBody @Valid BoardRequest request,
            @AuthenticationPrincipal UserAuth userAuth
    ) {
        BoardResponse response = boardService.updatePost(userAuth, postId, request);
        return ResponseEntity.ok(response);
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserAuth userAuth
    ) {
        boardService.deletePost(userAuth, postId);
        return ResponseEntity.ok().build();
    }

    // 게시글 단건 조회
    @GetMapping("/{postId}")
    public ResponseEntity<BoardResponse> getPost(@PathVariable Long postId) {
        boardServiceUtill.viewCount(postId);

        BoardResponse response = boardService.getPost(postId);  // Pageable 삭제
        return ResponseEntity.ok(response);
    }

    // 게시글 목록 조회 (페이징)
    @GetMapping
    public ResponseEntity<BoardPageResponse<BoardSimpleResponse>> getPosts(
            @RequestParam(defaultValue = "ALL") BoardCategory category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<BoardSimpleResponse> list = boardService.getPosts(category, keyword, page, size);
        return ResponseEntity.ok(BoardPageResponse.from(list));
    }

}


