package org.example.hansabal.pointHistory.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PointErrorCode implements ErrorCode {

	INSUFFICIENT_POINT(HttpStatus.BAD_REQUEST, "P001", "포인트가 부족합니다."),
	NO_POINT(HttpStatus.BAD_REQUEST, "P002", "포인트가 없습니다."),
	EXCEEDING_POINT_AMOUNT(HttpStatus.BAD_REQUEST, "P005", "사용하려는 포인트가 총 결제 금액보다 큽니다.");

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
