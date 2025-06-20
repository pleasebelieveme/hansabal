package org.example.hansabal.domain.chat.exception;

import org.example.hansabal.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements ErrorCode {
	INVALID_NICKNAME(HttpStatus.BAD_REQUEST,"CH001","존재하지 않은 닉네임입니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"CH002","권한이 없습니다. 로그인 먼저 해주세요");

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
