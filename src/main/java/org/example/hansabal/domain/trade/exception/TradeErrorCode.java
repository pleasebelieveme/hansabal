package org.example.hansabal.domain.trade.exception;

import org.example.hansabal.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TradeErrorCode implements ErrorCode {
	UNAUTHORIZED(HttpStatus.FORBIDDEN,"T001","본인이 게시한 거래글만 관리할 수 있습니다."),
	TRADE_NOT_FOUND(HttpStatus.NOT_FOUND,"T002","해당하는 거래를 찾을 수 없습니다."),
	NOT_SUPPORTED_TYPE(HttpStatus.BAD_REQUEST,"T003","올바르지 않은 상태값입니다."),
	CLOSED_CASE(HttpStatus.BAD_REQUEST,"T004","이미 완료된 거래입니다."),
	NOT_PAID(HttpStatus.BAD_REQUEST,"T005","무료가 아닌 거래는 바로 배송단계로 넘길 수 없습니다."),
	NOT_ALLOWED(HttpStatus.FORBIDDEN,"T006","사용 권한이 없거나 부족합니다."),
	WRONG_STAGE(HttpStatus.BAD_REQUEST, "T007","거래 요청의 상태가 이 기능을 허용하지 않습니다."),
	ALREADY_OCCUPIED(HttpStatus.BAD_REQUEST,"T008","이미 요청을 수락한 거래입니다."),
	REQUESTS_NOT_FOUND(HttpStatus.NOT_FOUND,"T009","해당하는 거래요청을 찾을 수 없습니다."),
	NOT_IDLE_REQUESTS(HttpStatus.BAD_REQUEST,"T010","이미 수락된 요청은 취소할 수 없습니다."),
	ALREADY_PAID(HttpStatus.BAD_REQUEST,"T011","이미 지불된 요청을 지불 전으로 돌릴 수 없습니다."),
	INVALID_TRADE_STATUS(HttpStatus.FORBIDDEN,"T012", "유효하지않은 교환상태"),
	PRODUCT_OWNER_MISMATCH(HttpStatus.BAD_REQUEST, "T013","주인이 맞지않습니다."),
	Trade_ALREADY_FINISHED(HttpStatus.BAD_REQUEST,"T014","거래가 이미 완료 됬습니다."),
	ALREADY_COMPLETED(HttpStatus.BAD_REQUEST,"T015","이미 완료 됬습니다."),
	Trade_NOT_FOUND(HttpStatus.NOT_FOUND,"T016","해당하는 거래요청을 찾을 수 없습니다.");


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
