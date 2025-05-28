package org.example.hansabal.domain.auth.exception;

import org.example.hansabal.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
	NOT_FOUND_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED,"액세스 토큰이 유효한 형태가 아닙니다."),
	NOT_FOUND_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,"리프레시 토큰이 유효한 형태가 아닙니다.");


	private final HttpStatus httpStatus;
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
