package org.example.hansabal.domain.board.entity;


import lombok.Getter;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.board.exception.BoardErrorCode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Getter
public enum BoardCategory {
    DAILY("일상"),
    QUESTION("질문"),
    INFO("정보"),
    REVIEW("후기"),
    ETC("기타"),
    ALL("전체"); // 조회용(실제 게시글 저장 시 사용 X)

    private final String displayName;

    BoardCategory(String displayName) {
        this.displayName = displayName;
    }

    // 한글명으로 enum 찾기 (예: DTO → Entity 변환 시 활용)
    public static BoardCategory fromDisplayName(String displayName) {
        for (BoardCategory category : BoardCategory.values()) {
            if (category.displayName.equals(displayName)) {
                return category;
            }
        }
        throw new BizException(BoardErrorCode.INVALID_CATEGORY);
    }

    @Component
    public class StringToBoardCategoryConverter implements Converter<String, BoardCategory> {
        @Override
        public BoardCategory convert(String source) {
            try {
                return BoardCategory.valueOf(source);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("올바르지 않은 카테고리 값입니다: " + source);
            }
        }
    }
}
