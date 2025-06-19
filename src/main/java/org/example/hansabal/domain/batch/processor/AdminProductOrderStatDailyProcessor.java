package org.example.hansabal.domain.batch.processor;

import org.example.hansabal.domain.admin.entity.ProductOrderStatDaily;
import org.example.hansabal.domain.batch.entity.AdminProductOrderStatDaily;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AdminProductOrderStatDailyProcessor implements ItemProcessor<ProductOrderStatDaily, AdminProductOrderStatDaily> {

	@Override
	public AdminProductOrderStatDaily process(ProductOrderStatDaily order) {
		// ✅ 핵심 변환 로직: ProductStatDaily → AdminProductOrderStatDaily
		// ProductStatDaily의 date, orderCount, totalSales만 사용 중

		// ⚠️ 개선 제안:
		// 만약 AdminProductOrderStatDaily에 ProductId 또는 adminId 등의 추가적인 필드가 있다면,
		// 해당 필드들도 매핑 로직에 포함해야 할 수 있음.
		// 현재는 최소 필드만 매핑 중이며, 컬럼 구조가 변경되었거나 확장되었다면 수정 필요.

		// ✅ 로그 추가 고려 (선택):
		// 디버깅이나 추적을 위해 아래와 같이 로그를 추가할 수 있음
		// log.debug("Processing ProductStatDaily: {}", order);

		return new AdminProductOrderStatDaily(order.getDate(), order.getOrderCount(), order.getTotalSales());
	}
}
