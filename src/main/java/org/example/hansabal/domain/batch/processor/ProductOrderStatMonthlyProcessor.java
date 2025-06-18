package org.example.hansabal.domain.batch.processor;


import org.example.hansabal.domain.admin.entity.ProductOrderStatDaily;
import org.example.hansabal.domain.admin.entity.ProductOrderStatId;
import org.example.hansabal.domain.admin.entity.ProductOrderStatMonthly;
import org.example.hansabal.domain.admin.entity.ProductStatDaily;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ProductOrderStatMonthlyProcessor implements ItemProcessor<ProductOrderStatDaily, ProductOrderStatMonthly> {

	@Override
	public ProductOrderStatMonthly process(ProductOrderStatDaily item) {
		// ✅ 1. 일 단위 통계로부터 ProductId 및 날짜 추출
		Long ProductId = item.getId().getProdcutId();

		// ✅ 2. 날짜를 월 단위로 변환 (해당 월의 1일로 통일)
		LocalDate date = item.getId().getDate().withDayOfMonth(1);

		// ✅ 3. 월별 통계용 복합키 생성
		ProductOrderStatId id = ProductOrderStatId.of(ProductId, date);

		// ✅ 4. 월별 통계 엔티티 생성 (주문 수, 총 매출)
		// 주의: 월별로 누적되지 않고 단일 일자의 값만 전달됨
		// → Writer 또는 DB에서 월 단위 집계 필요
		return ProductOrderStatMonthly.of(id, item.getOrderCount(), item.getTotalSales());
	}
}
