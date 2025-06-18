package org.example.hansabal.domain.order.dto.response;


import org.example.hansabal.domain.order.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponseDto(
	Long orderId,
	Long userId,
	Long ProductId,
	List<OrderItemDetailResponseDto> orderItemList,
	Integer totalPrice,
	OrderStatus status,
	LocalDateTime createdDate
) {

}
