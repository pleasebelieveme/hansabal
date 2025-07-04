package org.example.hansabal.domain.review.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateReviewRequest {
    @Lob
    @NotBlank(message = "내용은 필수 입력값입니다.")
    @JsonProperty("content")
    private final String content;

    @Min(value = 1, message = "평점은 1점 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 5점 이하여야 합니다.")
    @JsonProperty("rating")
    private final Integer rating;

    public UpdateReviewRequest(
            @JsonProperty("content") String content,
            @JsonProperty("rating") Integer rating) {
        this.content = content;
        this.rating = rating;
    }
}
