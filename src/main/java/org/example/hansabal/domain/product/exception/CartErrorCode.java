package org.example.hansabal.domain.product.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartErrorCode implements ErrorCode {
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "존재하지 않는 장바구니입니다."),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "C002", "수량은 1개 이상입니다."),
    CART_AUTHOR_MISMATCH(HttpStatus.FORBIDDEN, "C003", "장바구니 사용자와 일치하지 않습니다."),
    CART_ALREADY_EXISTS(HttpStatus.CONFLICT, "C004", "이미 장바구니가 존재합니다.");

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
