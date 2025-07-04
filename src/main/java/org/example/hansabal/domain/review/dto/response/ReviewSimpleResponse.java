package org.example.hansabal.domain.review.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ReviewSimpleResponse {

    private final String nickname;
    private final String content;
    private final Integer rating;

    @JsonCreator
    public ReviewSimpleResponse(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("content") String content,
            @JsonProperty("rating") Integer rating
    ) {
        this.nickname = nickname;
        this.content = content;
        this.rating = rating;
    }
}
