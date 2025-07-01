package org.example.hansabal.domain.trade.dto.response;


import org.example.hansabal.domain.trade.entity.TradeStatus;

import java.util.List;

public record TradeResponseDto(
		Long tradeId,
		Long userId,
		Long productId,
		List<Long> tradeItemIds,
		Integer totalPrice,
		TradeStatus status
) {

}
