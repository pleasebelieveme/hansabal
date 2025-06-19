package org.example.hansabal.domain.wallet.dto.response;

import org.example.hansabal.domain.wallet.entity.WalletHistory;

public record HistoryResponseDto(Long tradeId, String type, Long price, Long remain) {
	public static HistoryResponseDto from(WalletHistory walletHistory) {
		return new HistoryResponseDto(walletHistory.getTradeId(), walletHistory.getType(), walletHistory.getPrice(), walletHistory.getRemain());
	}
}
