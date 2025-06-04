package org.example.hansabal.domain.review.dto.request;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateReviewRequest {

    private final Long userId;

    @Lob
    @NotBlank(message = "내용은 필수 입력값입니다.")
    private final String content;

}
