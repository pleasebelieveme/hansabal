package org.example.hansabal.domain.trade.dto.response;

import java.util.List;
import org.example.hansabal.domain.trade.response.MenuOptionDetailResponseDto;

public record TradeItemDetailResponseDto(
		Long productId,
		String productName,
		List<MenuOptionDetailResponseDto> menuOptionDetailList,
		Integer quantity
) {
}
