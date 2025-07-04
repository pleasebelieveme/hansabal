package org.example.hansabal.domain.review.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewPageResult {

    private List<ReviewSimpleResponse> content; // Page의 데이터 리스트
    private int page;       // 현재 페이지 번호
    private int size;       // 페이지 크기
    private long totalElements;     // 전체 데이터 수

    public boolean isEmpty() {
        return content == null || content.isEmpty();
    }

    @JsonCreator
    public ReviewPageResult(
            @JsonProperty("content") List<ReviewSimpleResponse> content,
            @JsonProperty("page") int page,
            @JsonProperty("size") int size,
            @JsonProperty("totalElements") long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
    }

    public static ReviewPageResult of(Page<ReviewSimpleResponse> result) {
        return new ReviewPageResult(
                result.getContent(),
                result.getNumber() + 1,
                result.getSize(),
                result.getTotalElements()
        );
    }
}
