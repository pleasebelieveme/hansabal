package org.example.hansabal.domain.trade.dto.response;

import org.example.hansabal.domain.trade.entity.Trade;
import org.example.hansabal.domain.users.entity.User;

import lombok.Builder;

@Builder
public record TradeResponseDto(Long tradeId, String title, String contents, User trader) {
	public static TradeResponseDto from(Trade trade) {
		return new TradeResponseDto(trade.getTradeId(), trade.getTitle(), trade.getContents(), trade.getTrader());
	}
}
