package org.example.hansabal.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BizException extends RuntimeException {

	private final ErrorCode errorCode;

	public BizException(String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public BizException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
	public HttpStatus getStatus() {
		return errorCode.getStatus();
	}
	public String getCode() {
		return errorCode.getCode();
	}
	public String getErrorMessage() {
		return errorCode.getMessage();
	}
}
