package org.example.hansabal.domain.comment.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class CommentPageResult {
	private List<CommentPageResponse> contents;
	private int page;
	private int size;
	private long total;

	public boolean isEmpty(){
		return contents == null || contents.isEmpty();
	}

	public static CommentPageResult of(Page<CommentPageResponse> result){
		return new CommentPageResult(
			result.getContent(),
			result.getNumber() + 1,
			result.getSize(),
			result.getTotalElements()
		);
	}
}
