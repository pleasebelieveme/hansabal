package org.example.hansabal.domain.admin.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StatErrorCode implements ErrorCode {
	UNSUPPORTED_STAT_PERIOD(HttpStatus.NOT_FOUND, "A001", "지원하지 않는 통계 주기입니다.");

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
