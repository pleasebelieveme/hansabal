package org.example.hansabal.domain.wallet.exception;

import org.example.hansabal.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WalletErrorCode implements ErrorCode {
	NO_WALLET_FOUND(HttpStatus.NOT_FOUND,"W001","연결된 전자지갑을 찾을 수 없습니다."),
	NOT_SUPPORTED_TYPE(HttpStatus.BAD_REQUEST,"W002","올바르지 않은 입력값입니다."),
	DUPLICATE_WALLET_NOT_ALLOWED(HttpStatus.BAD_REQUEST,"W003","유저당 하나의 전자지갑만 소유할 수 있습니다."),
	HISTORY_NOT_EXIST(HttpStatus.NOT_FOUND,"W004","해당하는 거래 기록이 없습니다."),
	DATA_MISMATCH(HttpStatus.BAD_REQUEST,"W005","거래 기록과 거래가 일치하지 않습니다."),
	WRONG_REQUESTS_CONNECTED(HttpStatus.NOT_FOUND,"W006","연결된 거래요청을 찾을 수 없습니다."),
	NOT_ENOUGH_CASH(HttpStatus.BAD_REQUEST,"W007","잔액이 부족하여 거래를 진행할 수 없습니다."),
	CASH_LOAD_FAIL(HttpStatus.BAD_REQUEST,"W008","전자화폐 충전 결재 실패."),
	INVALID_ACCESS(HttpStatus.FORBIDDEN, "W009", "유효하지 않은 접근입니다."),
	IOEXCEPTION_FOUND(HttpStatus.INTERNAL_SERVER_ERROR,"W010","결제 IOE 발견"),
	INTERNAL_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"W011","기타 서비스오류 발견"),
	INCORRECT_VALUE_FOUND(HttpStatus.BAD_REQUEST,"W012","필수값 발견 실패");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public HttpStatus getStatus() {
		return httpStatus;
	}

	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
