package org.example.hansabal.domain.wallet.dto.response;

import org.example.hansabal.domain.wallet.entity.WalletHistory;

public record HistoryResponseDto(Long tradeId, Long price, Long remain) {
	public static HistoryResponseDto from(WalletHistory walletHistory) {
		return new HistoryResponseDto(walletHistory.getId(), walletHistory.getPrice(), walletHistory.getRemain());
	}
}
