package org.example.hansabal.domain.batch.processor;

import org.example.hansabal.domain.admin.entity.ProductTradeStatDaily;
import org.example.hansabal.domain.admin.entity.ProductTradeStatId;
import org.example.hansabal.domain.trade.entity.Trade;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class ProductTradeStatDailyProcessor implements ItemProcessor<Trade, ProductTradeStatDaily> {

	@Override
	public ProductTradeStatDaily process(Trade Trade) {
		// ✅ 1. 복합키 생성: ProductId + 날짜 기준
		// Trade의 createdDate를 LocalDate로 변환하여 일 단위 집계용 키 구성
		ProductTradeStatId id = ProductTradeStatId.of(
				Trade.getProduct().getId(),
				Trade.getCreatedAt().toLocalDate()
		);

		// ✅ 2. 일별 주문 건수 1건, 해당 주문의 총 결제 금액 사용
		// - 주문 1건당 호출되므로 건수는 1
		// - 누적은 이후 Writer 또는 DB Merge 단계에서 처리

		// ⚠️ 개선 포인트:
		// - null 방지: Trade.getProduct(), getCreatedDate(), getTotalPrice()에 대해 null 가능성 있는 경우 체크 필요
		// - getTotalPrice가 int일 경우 long 변환은 문제 없지만, 금액 단위에 따라 정확성 고려

		return ProductTradeStatDaily.of(
				id,
				1, // 주문 1건 처리
				Long.valueOf(Trade.getTotalPrice()) // 가격 정수형 변환
		);
	}
}
