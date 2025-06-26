package org.example.hansabal.domain.trade.dto.response;

import org.example.hansabal.domain.trade.entity.TradeStatus;
import java.time.LocalDateTime;
import java.util.List;

public record TradeDetailResponseDto(
		Long orderId,
		Long userId,
		Long storeId,
		List<TradeItemDetailResponseDto> TradeItemList,
		Integer totalPrice,
		TradeStatus status,
		LocalDateTime createdDate
) {

}
