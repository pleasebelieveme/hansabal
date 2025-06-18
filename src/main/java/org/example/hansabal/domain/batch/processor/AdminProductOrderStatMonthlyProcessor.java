package org.example.hansabal.domain.batch.processor;


import lombok.extern.slf4j.Slf4j;
import org.example.hansabal.domain.admin.entity.ProductOrderStatMonthly;
import org.example.hansabal.domain.batch.entity.AdminProductOrderStatMonthly;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AdminProductOrderStatMonthlyProcessor implements ItemProcessor<ProductOrderStatMonthly, AdminProductOrderStatMonthly> {

	@Override
	public AdminProductOrderStatMonthly process(ProductOrderStatMonthly order) {
		// ✅ 변환 로직: ProductStatMonthly → AdminProductOrderStatMonthly
		// 현재는 date, orderCount, totalSales 필드만 변환에 사용

		// ⚠️ 수정 검토 필요:
		// AdminProductOrderStatMonthly 엔티티에 추가 필드 (예: ProductId, category 등)가 있다면
		// 해당 필드를 포함해서 생성자 인자 또는 setter로 전달해야 함

		// ✅ 개선 제안 (선택사항):
		// 디버깅 또는 배치 결과 추적이 필요하다면 로그 추가 가능
		// log.debug("Converting Monthly Stat: {}", order);

		return new AdminProductOrderStatMonthly(order.getDate(), order.getOrderCount(), order.getTotalSales());
	}
}
