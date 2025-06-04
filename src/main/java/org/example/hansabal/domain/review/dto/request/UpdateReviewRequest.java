package org.example.hansabal.domain.review.dto.request;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateReviewRequest {
    @Lob
    @NotBlank(message = "내용은 필수 입력값입니다.")
    private final String content;

    public UpdateReviewRequest(String content) {
        this.content = content;
    }
}
