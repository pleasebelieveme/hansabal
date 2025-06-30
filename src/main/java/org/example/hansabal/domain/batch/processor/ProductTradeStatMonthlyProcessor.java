package org.example.hansabal.domain.batch.processor;


import org.example.hansabal.domain.admin.entity.ProductTradeStatDaily;
import org.example.hansabal.domain.admin.entity.ProductTradeStatId;
import org.example.hansabal.domain.admin.entity.ProductTradeStatMonthly;
import org.example.hansabal.domain.admin.entity.ProductStatDaily;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ProductTradeStatMonthlyProcessor implements ItemProcessor<ProductTradeStatDaily, ProductTradeStatMonthly> {

	@Override
	public ProductTradeStatMonthly process(ProductTradeStatDaily item) {
		// ✅ 1. 일 단위 통계로부터 ProductId 및 날짜 추출
		Long ProductId = item.getId().getProductId();

		// ✅ 2. 날짜를 월 단위로 변환 (해당 월의 1일로 통일)
		LocalDate date = item.getId().getDate().withDayOfMonth(1);

		// ✅ 3. 월별 통계용 복합키 생성
		ProductTradeStatId id = ProductTradeStatId.of(ProductId, date);

		// ✅ 4. 월별 통계 엔티티 생성 (주문 수, 총 매출)
		// 주의: 월별로 누적되지 않고 단일 일자의 값만 전달됨
		// → Writer 또는 DB에서 월 단위 집계 필요
		return ProductTradeStatMonthly.of(id, item.getTradeCount(), item.getTotalSales());
	}
}
