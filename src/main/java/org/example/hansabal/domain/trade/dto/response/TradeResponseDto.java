package org.example.hansabal.domain.trade.dto.response;

import com.siot.IamportRestClient.response.payco.OrderStatus;
import org.example.hansabal.domain.trade.entity.Trade;

import lombok.Builder;
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
