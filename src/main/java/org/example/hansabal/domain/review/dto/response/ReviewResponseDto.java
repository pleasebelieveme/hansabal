package org.example.hansabal.domain.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewResponseDto {

    private final String nickname;
    private final String content;
}
