package org.example.hansabal.domain.comment.dto.response;

import org.example.hansabal.domain.comment.entity.Comment;
import org.hibernate.validator.constraints.Length;

public record CommentResponse(
	String content
) {
	public static CommentResponse from(Comment comment){
		return new CommentResponse(comment.getContents());
	}
}
