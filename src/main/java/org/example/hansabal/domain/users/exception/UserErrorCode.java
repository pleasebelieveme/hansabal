package org.example.hansabal.domain.users.exception;

import org.example.hansabal.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
	DUPLICATE_USER_ID(HttpStatus.BAD_REQUEST, "U001", "중복된 아이디입니다."),
	NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "U002", "존재하지 않는 사용자입니다."),
	INVALID_REQUEST(HttpStatus.BAD_REQUEST, "U003", "유효하지 않은 요청입니다."),
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U004",  "유효하지 않은 비밀번호입니다.");

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