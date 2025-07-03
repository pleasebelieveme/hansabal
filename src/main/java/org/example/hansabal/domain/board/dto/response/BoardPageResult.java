package org.example.hansabal.domain.board.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

public record BoardPageResult(
	List<BoardSimpleResponse> contents,
	int page,
	int size,
	long totalElements
) {
	public boolean isEmpty(){
		return contents == null || contents.isEmpty();
	}

	public static  BoardPageResult of(Page<BoardSimpleResponse> result) {
		return new BoardPageResult(
			result.getContent(),
			result.getNumber() + 1,
			result.getSize(),
			result.getTotalElements()
		);
	}
}

