package org.example.hansabal.domain.order.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {

	UNDER_MINIMUM_ORDER_PRICE(HttpStatus.BAD_REQUEST, "O001", "주문하려는 금액이 최소주문금액보다 작습니다."),
	Product_CLOSED(HttpStatus.BAD_REQUEST, "O002", "매장이 영업 중이 아닙니다."),
	INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "O003", "현재 주문 상태와 일치하지 않습니다."),
	ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O004", "존재하지 않는 주문입니다."),
	ORDER_ALREADY_FINISHED(HttpStatus.BAD_REQUEST, "O005", "이미 완료된 주문입니다."),
	INVALID_ORDER_REJECTION(HttpStatus.BAD_REQUEST, "O006", "거절할 수 없는 상태입니다."),
	INACCESSIBLE_ORDER(HttpStatus.FORBIDDEN, "O007", "접근할 수 없는 주문입니다."),
	PRODUCT_OWNER_MISMATCH(HttpStatus.FORBIDDEN,"O008" ,"주문 불일치입니다." );

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public HttpStatus getStatus() {
		return httpStatus;
	}

	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
