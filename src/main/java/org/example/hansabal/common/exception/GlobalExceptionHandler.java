package org.example.hansabal.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(BizException.class)
	public ResponseEntity<ErrorResponse> handleBizError(BizException exception) {
		return ResponseEntity
			.status(exception.getErrorCode().getStatus())
			.body(ErrorResponse.of(exception.getErrorCode()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException exception) {
		return ResponseEntity
			.badRequest()
			.body(ErrorResponse.of(CommonErrorCode.INVALID_INPUT_VALUE, exception.getBindingResult()));
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<String> handleMissingServletRequestParameter(
		MissingServletRequestParameterException exception) {
		String missingParam = exception.getParameterName();
		String message = String.format("필수 파라미터 '%s'가 없습니다.", missingParam);
		return ResponseEntity.badRequest().body(message);
	}
}
