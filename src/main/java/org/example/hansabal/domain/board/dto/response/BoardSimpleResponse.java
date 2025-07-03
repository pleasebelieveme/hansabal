package org.example.hansabal.domain.board.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.board.entity.BoardCategory;

import com.fasterxml.jackson.annotation.JsonProperty;

@RequiredArgsConstructor
@Getter
public class BoardSimpleResponse {

    private final String writer;
    private final BoardCategory category;
    private final String title;
    private final Integer viewCount;
    private final Integer dibCount;
}
