package org.example.hansabal.domain.order.aop;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.example.hansabal.domain.order.dto.response.OrderResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OrderLoggingAspect {

	private static final Logger logger = LoggerFactory.getLogger(OrderLoggingAspect.class);

	@AfterReturning(value = "@annotation(LogOrderCreation)", returning = "result")
	public void logOrderCreation(JoinPoint joinPoint, Object result) {
		if (result instanceof OrderResponseDto orderResponseDto) {
			logger.info("주문 성공! 주문 ID: {}, 사용자 ID: {}, 가게 ID: {}, 총 결제 금액: {}",
				orderResponseDto.orderId(),
				orderResponseDto.userId(),
				orderResponseDto.ProductId(),
				orderResponseDto.totalPrice());
		}
	}
}