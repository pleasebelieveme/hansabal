package org.example.hansabal.domain.comment.exception;

import org.example.hansabal.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DibErrorCode implements ErrorCode {
	INVALID_ID(HttpStatus.BAD_REQUEST,"D001","유효하지 않은 ID 입니다."),
	ALREADY_DIBBED(HttpStatus.BAD_REQUEST,"D002","이미 좋아요를 눌렀습니다.");

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
