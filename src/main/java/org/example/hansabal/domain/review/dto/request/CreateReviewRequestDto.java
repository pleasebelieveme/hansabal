package org.example.hansabal.domain.review.dto.request;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.PrintWriter;

@Getter
@AllArgsConstructor
public class CreateReviewRequestDto {
    private final Long userId;
    @Lob
    private final String content;

}
