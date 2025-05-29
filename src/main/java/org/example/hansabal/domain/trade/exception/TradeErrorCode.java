package org.example.hansabal.domain.trade.exception;

import org.example.hansabal.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TradeErrorCode implements ErrorCode {
	Unauthorized(HttpStatus.FORBIDDEN,"T001","본인이 게시한 거래글만 관리할 수 있습니다."),
	NoSuchThing(HttpStatus.NOT_FOUND,"T002","입력값 중 찾을 수 없는 개체가 있습니다.");

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
