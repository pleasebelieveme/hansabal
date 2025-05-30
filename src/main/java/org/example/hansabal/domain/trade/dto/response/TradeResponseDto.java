package org.example.hansabal.domain.trade.dto.response;

import org.example.hansabal.domain.trade.entity.Trade;

import lombok.Builder;

@Builder
public record TradeResponseDto(Long tradeId, String title, String contents, Long trader, String traderName) {
	public static TradeResponseDto from(Trade trade) {
		return new TradeResponseDto(trade.getTradeId(), trade.getTitle(), trade.getContents(), trade.getTrader().getId(), trade.getTrader().getName());
	}
}
