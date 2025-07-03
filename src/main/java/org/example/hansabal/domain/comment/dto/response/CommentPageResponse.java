package org.example.hansabal.domain.comment.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,include = JsonTypeInfo.As.PROPERTY,property = "@class")
public class CommentPageResponse {
	private String comments;
	private int dibCount;

	public CommentPageResponse(String comments, Integer dibCount) {
		this.comments = comments;
		this.dibCount = dibCount;
	}
}
