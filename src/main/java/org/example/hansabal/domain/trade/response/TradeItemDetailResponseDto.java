package org.example.hansabal.domain.trade.response;

import java.util.List;
import org.example.hansabal.domain.trade.response.MenuOptionDetailResponseDto;

public record TradeItemDetailResponseDto(
	Long menuId,
	String name,
	List<MenuOptionDetailResponseDto> menuOptionDetailList,
	Integer quantity
) {
}
