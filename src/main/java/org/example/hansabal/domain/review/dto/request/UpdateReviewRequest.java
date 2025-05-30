package org.example.hansabal.domain.review.dto.request;

import jakarta.persistence.Lob;
import lombok.Getter;

@Getter
public class UpdateReviewRequest {

    @Lob
    private final String content;

    public UpdateReviewRequest(String content) {
        this.content = content;
    }
}
