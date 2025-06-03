package org.example.hansabal.domain.comment.dto.response;

import org.example.hansabal.domain.comment.entity.Comment;




public record CommentResponse(
	String contents
) {
	public static CommentResponse from(Comment comment){
		return new CommentResponse(comment.getContents());
	}
}
