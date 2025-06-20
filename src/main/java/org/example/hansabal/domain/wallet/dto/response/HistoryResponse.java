package org.example.hansabal.domain.wallet.dto.response;

import org.example.hansabal.domain.wallet.entity.WalletHistory;

public record HistoryResponse(Long tradeId, String type, Long price, Long remain) {
	public static HistoryResponse from(WalletHistory walletHistory) {
		return new HistoryResponse(walletHistory.getTradeId(), walletHistory.getType(), walletHistory.getPrice(), walletHistory.getRemain());
	}
}
