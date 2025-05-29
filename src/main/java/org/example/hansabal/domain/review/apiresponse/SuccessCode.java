package org.example.hansabal.domain.review.apiresponse;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessCode {
    REVIEW_CREATE_SUCCESS(201, HttpStatus.CREATED, "리뷰가 작성되었습니다."),
    REVIEW_GET_SUCCESS(200, HttpStatus.OK, "리뷰가 조회되었습니다.."),
    REVIEW_PUT_SUCCESS(200, HttpStatus.OK, "리뷰가 수정되었습니다."),
    REVIEW_DELETE_SUCCESS(200, HttpStatus.OK, "리뷰가 삭제되었습니다.");

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;

    SuccessCode(Integer code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
