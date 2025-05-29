package org.example.hansabal.domain.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.hansabal.domain.review.entity.Review;

@Getter
@AllArgsConstructor
public class createReviewResponseDto {

    private final String nickname;
    private final String content;

    public static createReviewResponseDto from(Review review) {
        return new createReviewResponseDto(review.getUser().getNickname(),review.getContent());
    }
}
