package org.example.hansabal.domain.order.dto.request;

import jakarta.validation.constraints.NotNull;
import org.example.hansabal.domain.order.entity.OrderStatus;

public record OrderStatusRequestDto(
	@NotNull(message = "주문 상태는 필수 입력 값입니다.")
	OrderStatus orderStatus
) {
}
