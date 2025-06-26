package org.example.hansabal.domain.trade.dto.response;

import org.example.hansabal.domain.trade.entity.Trade;

import lombok.Builder;

@Builder
public record TradeResponse(Long tradeId, String title, String contents, Long trader, Long price, String traderNickname) {
	public static TradeResponse from(Trade trade) {
		return new TradeResponse(
				trade.getId(),
				trade.getTitle(),
				trade.getContents(),
				trade.getTrader().getId(),
				trade.getPrice(),
				trade.getTrader().getNickname());
	}
}
