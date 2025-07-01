package org.example.hansabal.domain.comment.dto.response;

import java.util.List;

public record CommentPageResult(
	List<CommentPageResponse> contents,
	int page,
	int size,
	long total
) {
	public boolean isEmpty(){
		return contents == null || contents.isEmpty();
	}
}
