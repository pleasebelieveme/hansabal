package org.example.hansabal.domain.board.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,include = JsonTypeInfo.As.PROPERTY,property = "@class")
public class BoardPageResult{

	private List<BoardSimpleResponse> contents;
	private int page;
	private int size;
	private long totalElements;

	public boolean isEmpty() {
		return contents == null || contents.isEmpty();
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

