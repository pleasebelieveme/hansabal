package org.example.hansabal.domain.review.dto.request;

import jakarta.persistence.Lob;
import lombok.Getter;

@Getter
public class UpdateReviewRequestDto {


    @Lob
    private final String content;

    public UpdateReviewRequestDto(String content) {
        this.content = content;
    }
}
