package org.example.hansabal.domain.review.dto.request;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateReviewRequest {
    private final Long userId;
    @Lob
    private final String content;

}
