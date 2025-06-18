package org.example.hansabal.domain.order.response;


import org.example.hansabal.domain.order.entity.OrderStatus;

import java.util.List;

public record OrderResponseDto(
	Long orderId,
	Long userId,
	Long ProductId,
	List<Long> orderItemIds,
	Integer totalPrice,
	OrderStatus status
) {

}
