package org.example.hansabal.domain.trade.aop;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.example.hansabal.domain.trade.dto.response.TradeResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TradeLoggingAspect {

	private static final Logger logger = LoggerFactory.getLogger(TradeLoggingAspect.class);

	@AfterReturning(value = "@annotation(LogTradeCreation)", returning = "result")
	public void logTradeCreation(JoinPoint joinPoint, Object result) {
		if (result instanceof TradeResponseDto tradeResponseDto) {
			logger.info("주문 성공! 주문 ID: {}, 사용자 ID: {}, 상품 ID: {}, 총 결제 금액: {}",
					tradeResponseDto.tradeId(),
					tradeResponseDto.trader(),
					tradeResponseDto.title(),
					tradeResponseDto.traderNickname()
			);
		}
	}
}