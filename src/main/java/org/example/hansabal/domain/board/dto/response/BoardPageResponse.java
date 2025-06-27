package org.example.hansabal.domain.board.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record BoardPageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements
){
    public static <T> BoardPageResponse<T> from(Page<T> page) {
        return new BoardPageResponse<>(
                page.getContent(),
                page.getNumber() + 1, // 페이지 0부터 시작하면 +1
                page.getSize(),
                page.getTotalElements()
        );
    }
}
