package org.example.hansabal.domain.wallet.exception;

import org.example.hansabal.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WalletErrorCode implements ErrorCode {
	NO_SUCH_THING(HttpStatus.NOT_FOUND,"W001","입력값으로 찾을 수 없는 개체가 있습니다."),
	NOT_SUPPORTED_TYPE(HttpStatus.BAD_REQUEST,"W002","올바르지 않은 입력값입니다."),
	DUPLICATE_WALLET_NOT_ALLOWED(HttpStatus.BAD_REQUEST,"W003","유저당 하나의 전자지갑만 소유할 수 있습니다."),
	HISTORY_NOT_EXIST(HttpStatus.NOT_FOUND,"W004","해당하는 거래 기록이 없습니다."),
	DATA_MISSMATCH(HttpStatus.BAD_REQUEST,"W005","거래 기록과 거래가 일치하지 않습니다.");

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
