package org.example.anonymous.domain.product.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode{
        INVALID_ID(HttpStatus.BAD_REQUEST,"F001" ,"유효하지 않은 아이디입니다."),
        INVALID_NAME(HttpStatus.BAD_REQUEST,"F002" ,"중복된 이름입니다."),
        INVALID_PRODUCTSTATUS(HttpStatus.BAD_REQUEST,"F003" ,"해당 상품의 재고가 없습니다."),
        DUPLICATED_LIST(HttpStatus.BAD_REQUEST,"F004","해당 상품이 장바구니에 이미 존재합니다."),
        NO_CONTENTS(HttpStatus.NO_CONTENT,"F005","장바구니가 비었습니다."),
        LOCK_FAILED(HttpStatus.REQUEST_TIMEOUT,"F006","락 시간 초과로 요청 실패"),
        LOCK_INTERRUPTED(HttpStatus.BAD_REQUEST,"F007","락이 예기치않게 중단되었습니다.");



        private final HttpStatus httpStatus;
        private final String code;
        private final String message;

        @Override
        public int getStatus() {
            return httpStatus.value();
        }

        @Override
        public String getCode() {
            return this.name();
        }

        @Override
        public String getMessage() {
            return message;
        }

}
