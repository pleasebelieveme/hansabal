package org.example.hansabal.domain.product.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
        INVALID_ID(HttpStatus.BAD_REQUEST,"P001" ,"유효하지 않은 아이디입니다."),
        INVALID_NAME(HttpStatus.BAD_REQUEST,"P002" ,"중복된 이름입니다."),
        INVALID_PRODUCTSTATUS(HttpStatus.BAD_REQUEST,"P003" ,"해당 상품의 재고가 없습니다."),
        DUPLICATED_LIST(HttpStatus.BAD_REQUEST,"P004","해당 상품이 장바구니에 이미 존재합니다."),
        NO_CONTENTS(HttpStatus.NO_CONTENT,"P005","장바구니가 비었습니다."),
        LOCK_FAILED(HttpStatus.REQUEST_TIMEOUT,"P006","락 시간 초과로 요청 실패"),
        LOCK_INTERRUPTED(HttpStatus.BAD_REQUEST,"P007","락이 예기치않게 중단되었습니다."),
        FAILED_NAVER(HttpStatus.BAD_GATEWAY, "P008", "네이버 API 서비스를 사용할 수 없습니다."),
        PRODUCT_NOT_FOUND(HttpStatus.BAD_REQUEST,"P009" ,"해당 상품의 재고가 없습니다.");




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
