package org.example.hansabal.domain.order.response;

import java.util.List;

public record OrderItemDetailResponseDto(
	Long menuId,
	String name,
	List<MenuOptionDetailResponseDto> menuOptionDetailList,
	Integer quantity
) {
}
