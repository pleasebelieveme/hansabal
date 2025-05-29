package org.example.hansabal.domain.review.dto.request;

import lombok.Getter;

@Getter
public class UpdateReviewRequestDto {

    private final String content;

    public UpdateReviewRequestDto(String content) {
        this.content = content;

    }
}
