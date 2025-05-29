package org.example.hansabal.domain.comment.controller;

import org.example.hansabal.domain.comment.dto.request.CreateCommentRequest;
import org.example.hansabal.domain.comment.dto.response.CommentResponse;
import org.example.hansabal.domain.comment.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

	@PostMapping("/boards/{boardId}/user/{userId}")
	public ResponseEntity<CommentResponse> createComment(
		@Valid @RequestBody CreateCommentRequest request,
		@PathVariable Long userId,
		@PathVariable Long boardId){

		CommentResponse comment = commentService.createComment(request, userId, boardId);

		return ResponseEntity.status(HttpStatus.CREATED).body(comment);
	}
}
