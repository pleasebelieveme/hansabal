package org.example.hansabal.domain.users.exception;

import org.example.hansabal.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
	DUPLICATE_PHONE_NUMBER(HttpStatus.BAD_REQUEST,"중복된 전화번호입니다."),
	DUPLICATE_USER_ID(HttpStatus.BAD_REQUEST,"중복된 아이디입니다."),
	NOT_FOUND_USER(HttpStatus.BAD_REQUEST,"존재하지 않는 사용자입니다."),
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "유효하지 않은 비밀번호입니다.");

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