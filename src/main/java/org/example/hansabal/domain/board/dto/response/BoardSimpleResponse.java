package org.example.hansabal.domain.board.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.board.entity.BoardCategory;

import com.fasterxml.jackson.annotation.JsonProperty;

@RequiredArgsConstructor
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,include = JsonTypeInfo.As.PROPERTY,property = "@class")
public class BoardSimpleResponse {

    private String writer;
    private BoardCategory category;
    private String title;
    private int viewCount;
    private int dibCount;

    public BoardSimpleResponse(String writer, BoardCategory category, String title, Integer viewCount, Integer dibCount) {
        this.writer = writer;
        this.category = category;
        this.title = title;
        this.viewCount = viewCount;
        this.dibCount = dibCount;
    }
}
