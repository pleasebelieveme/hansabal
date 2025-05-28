package org.example.hansabal.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "C002", "인증이 필요합니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN.value(), "C003", "권한이 없습니다."),
	POST_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "C004", "해당 게시글을 찾을 수 없습니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "C005", "해당 유저를 찾을 수 없습니다."),
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST.value(), "C001", "입력값이 올바르지 않습니다.");

	private final int status;
	private final String code;
	private final String message;


}
