package org.example.hansabal.domain.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.hansabal.domain.review.entity.Review;

@Getter
@AllArgsConstructor
public class ReviewResponse {

    private final Long id;
    private final String nickname;
    private final String content;

    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                 review.getId()
                ,review.getUser().getNickname()
                ,review.getContent());
    }
}
