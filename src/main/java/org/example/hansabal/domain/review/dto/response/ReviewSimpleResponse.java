package org.example.hansabal.domain.review.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReviewSimpleResponse {

    private final String nickname;
    private final String content;
    private final Integer rating;
}
