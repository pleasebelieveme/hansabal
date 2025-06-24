package org.example.hansabal.domain.auth.exception;

import org.example.hansabal.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
	INVALID_AUTH_HEADER(HttpStatus.UNAUTHORIZED, "A001", "Authorization 헤더 형식이 잘못되었습니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "리프레시 토큰이 유효하지 않습니다."),
	INVALID_REFRESH_TOKEN_SIGNATURE(HttpStatus.UNAUTHORIZED, "A003", "리프레시 토큰 서명이 유효하지 않습니다."),
	EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A004", "리프레시 토큰이 만료되었습니다. 다시 로그인하세요."),
	MISMATCHED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A005", "리프레시 토큰 정보가 일치하지 않습니다."),
	REUSED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A006", "이미 사용된 리프레쉬 토큰입니다."),
	OAUTH2_PROVIDER_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "A007", "지원하지 않는 OAuth2 제공자입니다."),
	OAUTH2_EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "A008", "이메일이 존재하지 않습니다.");

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
