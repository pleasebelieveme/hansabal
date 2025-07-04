package org.example.hansabal.domain.board.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BoardPageResult {

	private List<BoardSimpleResponse> contents;
	private int page;
	private int size;
	private long totalElements;

	public boolean isEmpty() {
		return contents == null || contents.isEmpty();
	}

	@JsonCreator
	public BoardPageResult(
			@JsonProperty("contents") List<BoardSimpleResponse> contents,
			@JsonProperty("page") int page,
			@JsonProperty("size") int size,
			@JsonProperty("totalElements") long totalElements
	) {
		this.contents = contents;
		this.page = page;
		this.size = size;
		this.totalElements = totalElements;
	}

	public static BoardPageResult of(Page<BoardSimpleResponse> result) {
		return new BoardPageResult(
				result.getContent(),
				result.getNumber() + 1,
				result.getSize(),
				result.getTotalElements()
		);
	}
}

