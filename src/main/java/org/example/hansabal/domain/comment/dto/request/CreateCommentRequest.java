package org.example.hansabal.domain.comment.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(
	@NotBlank
	@Length(max = 255)
	String content
) {
}
