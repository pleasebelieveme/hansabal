package org.example.hansabal.domain.batch.processor;

import org.example.hansabal.domain.admin.entity.ProductTradeStatDaily;
import org.example.hansabal.domain.batch.entity.AdminProductTradeStatDaily;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AdminProductTradeStatDailyProcessor implements ItemProcessor<ProductTradeStatDaily, AdminProductTradeStatDaily> {

	@Override
	public AdminProductTradeStatDaily process(ProductTradeStatDaily Trade) {
		// ✅ 핵심 변환 로직: ProductStatDaily → AdminProductTradeStatDaily
		// ProductStatDaily의 date, TradeCount, totalSales만 사용 중

		// ⚠️ 개선 제안:
		// 만약 AdminProductTradeStatDaily에 ProductId 또는 adminId 등의 추가적인 필드가 있다면,
		// 해당 필드들도 매핑 로직에 포함해야 할 수 있음.
		// 현재는 최소 필드만 매핑 중이며, 컬럼 구조가 변경되었거나 확장되었다면 수정 필요.

		// ✅ 로그 추가 고려 (선택):
		// 디버깅이나 추적을 위해 아래와 같이 로그를 추가할 수 있음
		// log.debug("Processing ProductStatDaily: {}", Trade);

		return new AdminProductTradeStatDaily(Trade.getDate(), Trade.getTradeCount(), Trade.getTotalSales());
	}
}
