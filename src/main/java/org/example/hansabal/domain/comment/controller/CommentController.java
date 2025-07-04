package org.example.hansabal.domain.comment.controller;

import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.comment.dto.request.CreateCommentRequest;
import org.example.hansabal.domain.comment.dto.response.CommentPageResponse;
import org.example.hansabal.domain.comment.dto.response.CommentPageResult;
import org.example.hansabal.domain.comment.dto.response.CommentResponse;
import org.example.hansabal.domain.comment.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Getter
public class CommentController {

	private final CommentService commentService;

	@PostMapping("/boards/{boardId}")
	public ResponseEntity<CommentResponse> createComment(
		@Valid @RequestBody CreateCommentRequest request,
		@AuthenticationPrincipal UserAuth userAuth,
		@PathVariable Long boardId){

		CommentResponse comment = commentService.createComment(request, userAuth, boardId);

		return ResponseEntity.status(HttpStatus.CREATED).body(comment);
	}

	@PatchMapping("/{commentId}")
	public ResponseEntity<CommentResponse> updateComment(
		@Valid @RequestBody CreateCommentRequest request,
		@PathVariable Long commentId,
		@AuthenticationPrincipal UserAuth userAuth){
		CommentResponse response = commentService.updateComment(request, commentId, userAuth);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/{boardId}")
	public ResponseEntity<CommentPageResult> findAllCommentsFromBoard(
		@PathVariable Long boardId,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "100") int size){

		CommentPageResult responses = commentService.findAllCommentsFromBoard(boardId, page, size);

		return ResponseEntity.status(HttpStatus.OK).body(responses);
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> deleteComment(@PathVariable Long commentId){

		commentService.deleteComment(commentId);

		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
