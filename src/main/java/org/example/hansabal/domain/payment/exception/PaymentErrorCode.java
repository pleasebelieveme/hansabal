package org.example.hansabal.domain.payment.exception;

import org.example.hansabal.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {
	SUSPICIOUS_VALUE_FOUND(HttpStatus.BAD_REQUEST,"P001","결제금액 위변조 의심."),
	LOAD_FAILED(HttpStatus.BAD_REQUEST,"P002","포트원 결재 실패"),
	IOEXCEPTION_FOUND(HttpStatus.INTERNAL_SERVER_ERROR,"P003","IO 예외 발견");

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
