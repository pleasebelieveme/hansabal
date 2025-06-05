package org.example.hansabal.domain.trade.exception;

import org.example.hansabal.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TradeErrorCode implements ErrorCode {
	UNAUTHORIZED(HttpStatus.FORBIDDEN,"T001","본인이 게시한 거래글만 관리할 수 있습니다."),
	NO_SUCH_THING(HttpStatus.NOT_FOUND,"T002","입력값 중 찾을 수 없는 개체가 있습니다."),
	NOT_SUPPORTED_TYPE(HttpStatus.BAD_REQUEST,"T003","올바르지 않은 입력값입니다."),
	CLOSED_CASE(HttpStatus.BAD_REQUEST,"T004","이미 완료된 거래입니다."),
	NOT_PAID(HttpStatus.BAD_REQUEST,"T005","무료가 아닌 거래는 바로 배송단계로 넘길 수 없습니다."),
	NOT_ALLOWED(HttpStatus.FORBIDDEN,"T006","사용 권한이 없거나 부족합니다."),
	WRONG_STAGE(HttpStatus.BAD_REQUEST, "T007","거래 요청의 상태가 이 기능을 허용하지 않습니다.");



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
