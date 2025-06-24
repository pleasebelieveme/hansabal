package org.example.hansabal.domain.trade.response;


import org.example.hansabal.domain.trade.entity.TradeStatus;

import java.util.List;

public record TradeResponseDto(
	Long TradeId,
	Long userId,
	Long ProductId,
	List<Long> TradeItemIds,
	Integer totalPrice,
	TradeStatus status
) {

}
