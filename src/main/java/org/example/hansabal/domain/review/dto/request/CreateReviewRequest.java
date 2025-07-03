package org.example.hansabal.domain.review.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateReviewRequest {

    //CreateReviewRequest 클래스의 필드가 final로 선언되어 있습니다. 이 경우 Jackson은 자동으로 역직렬화를 지원하지 못할 수 있습니다
    //@JsonProperty("content") // JSON 키와 필드를 매핑
    @NotBlank(message = "내용은 필수 입력값입니다.")
    @JsonProperty("content")
    private final String content;

    @Min(value = 1, message = "평점은 1점 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 5점 이하여야 합니다.")
    @JsonProperty("rating")
    private final Integer rating;


    @JsonCreator
    public CreateReviewRequest(
            @JsonProperty("content") String content,
            @JsonProperty("rating") Integer rating) {
        this.content = content;
        this.rating = rating;
    }
}
