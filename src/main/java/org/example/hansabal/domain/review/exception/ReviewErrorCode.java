package org.example.hansabal.domain.review.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {
    REVIEW_NOT_FOUND(HttpStatus.BAD_REQUEST,"R001","해당하는 리뷰가 없습니다"),
    RIVIEW_NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND, "R002", "해당하는 제품이 없습니다."),
    REVIEW_FORBIDDEN(HttpStatus.FORBIDDEN,"R003","권한이 없습니다."),
    REVIEW_ALREADY_EXISTS(HttpStatus.BAD_REQUEST,"R004", "해당 상품에 대한 리뷰를 이미 작성하셨습니다."),
    REVIEW_NO_SEARCH_QUERY(HttpStatus.NOT_FOUND,"R005", "검색 조건이 없습니다");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;


    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
