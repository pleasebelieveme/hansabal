package org.example.hansabal.domain.auth.exception;

import org.example.hansabal.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
	NOT_FOUND_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "A001", "액세스 토큰이 유효한 형태가 아닙니다."),
	NOT_FOUND_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A002","리프레시 토큰이 유효한 형태가 아닙니다."),
	INVALID_REFRESH_TOKEN_SIGNATURE(HttpStatus.UNAUTHORIZED, "A003", "리프레시 토큰 서명이 유효하지 않습니다."),
	EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A004", "리프레시 토큰이 만료되었습니다."),
	MISMATCHED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A005", "리프레시 토큰 정보가 일치하지 않습니다."),
	REUSED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A006", "이미 사용된 리프레쉬 토큰입니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public int getStatus() {
		return httpStatus.value();
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
