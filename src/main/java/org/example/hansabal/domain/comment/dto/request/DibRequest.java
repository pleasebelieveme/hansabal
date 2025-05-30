package org.example.hansabal.domain.comment.dto.request;

import org.example.hansabal.domain.comment.entity.DibType;

import jakarta.validation.constraints.NotNull;

public record DibRequest(
	@NotNull
	DibType dibType,
	@NotNull
	Long targetId
) {
}
