package org.example.hansabal.common.exception;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.validation.BindingResult;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
	private final int status;
	private final String code;
	private final String message;
	private final LocalDateTime timestamp;
	private final List<FieldError> errors;

	public static ErrorResponse of(ErrorCode errorCode) {
		return ErrorResponse.builder()
			.status(errorCode.getStatus())
			.code(errorCode.getCode())
			.message(errorCode.getMessage())
			.timestamp(LocalDateTime.now())
			.errors(Collections.emptyList())
			.build();
	}

	public static ErrorResponse of(ErrorCode errorCode, BindingResult bindingResult) {
		return ErrorResponse.builder()
			.status(errorCode.getStatus())
			.code(errorCode.getCode())
			.message(errorCode.getMessage())
			.timestamp(LocalDateTime.now())
			.errors(FieldError.of(bindingResult))
			.build();
	}

	@Getter
	@AllArgsConstructor
	public static class FieldError {

		private String field;
		private String rejectedValue;
		private String reason;

		public static List<FieldError> of(BindingResult bindingResult) {
			return bindingResult.getFieldErrors()
				.stream()
				.map(error -> new FieldError(error.getField(),
					error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
					error.getDefaultMessage()))
				.collect(Collectors.toList());
		}
	}
}
