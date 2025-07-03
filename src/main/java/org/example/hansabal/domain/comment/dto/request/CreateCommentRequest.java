package org.example.hansabal.domain.comment.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
	@NotBlank
	@Size(max = 255)
	String contents
) {
}
