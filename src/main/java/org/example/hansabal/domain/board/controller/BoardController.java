package org.example.hansabal.domain.board.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.board.dto.request.BoardRequest;
import org.example.hansabal.domain.board.dto.response.BoardResponse;
import org.example.hansabal.domain.board.entity.BoardCategory;
import org.example.hansabal.domain.board.service.BoardService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    // 게시글 등록
    @PostMapping
    public ResponseEntity<BoardResponse> createPost(
            @RequestBody @Valid BoardRequest request,
            @AuthenticationPrincipal UserAuth userAuth
    ) {
        BoardResponse response = boardService.createPost(userAuth, request);
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
        BoardResponse response = boardService.getPost(postId);
        return ResponseEntity.ok(response);
    }

    // 게시글 목록 조회 (페이징)
    @GetMapping
    public ResponseEntity<Page<BoardResponse>> getPosts(
            @RequestParam String category, // "ALL" 또는 "DAILY" 등
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        BoardCategory boardCategory = BoardCategory.valueOf(category.toUpperCase());
        Page<BoardResponse> list = boardService.getPosts(boardCategory, page, size);
        return ResponseEntity.ok(list);
    }

    // 게시글 검색 (페이징)
    @GetMapping("/search")
    public ResponseEntity<Page<BoardResponse>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<BoardResponse> list = boardService.searchPosts(keyword, page, size);
        return ResponseEntity.ok(list);
    }
}


