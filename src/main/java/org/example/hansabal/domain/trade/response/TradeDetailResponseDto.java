package org.example.hansabal.domain.trade.response;


import org.example.hansabal.domain.trade.entity.TradeStatus;

import java.time.LocalDateTime;
import java.util.List;

public record TradeDetailResponseDto(
	Long TradeId,
	Long userId,
	Long ProductId,
	List<Long> TradeItemList,
	Integer totalPrice,
	TradeStatus status,
	LocalDateTime createdDate
) {

}
