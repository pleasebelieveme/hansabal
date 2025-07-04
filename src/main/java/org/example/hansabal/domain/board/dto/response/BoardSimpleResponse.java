package org.example.hansabal.domain.board.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import org.example.hansabal.domain.board.entity.BoardCategory;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
public class BoardSimpleResponse {

    private final String writer;
    private final BoardCategory category;
    private final String title;
    private final int viewCount;
    private final int dibCount;
    @JsonCreator
    public BoardSimpleResponse(
            @JsonProperty("writer") String writer,
            @JsonProperty("category") BoardCategory category,
            @JsonProperty("title") String title,
            @JsonProperty("viewCount") int viewCount,
            @JsonProperty("dibCount") int dibCount
    ) {
        this.writer = writer;
        this.category = category;
        this.title = title;
        this.viewCount = viewCount;
        this.dibCount = dibCount;
    }

}
