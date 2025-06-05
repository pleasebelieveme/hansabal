package org.example.hansabal.domain.comment.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommentPageResponse {
	private final String comments;
	private final int dibCount;
}
