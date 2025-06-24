package org.example.hansabal.domain.batch.writer;


import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.admin.entity.ProductTradeStatDaily;
import org.example.hansabal.domain.admin.entity.ProductTradeStatId;
import org.example.hansabal.domain.admin.entity.ProductStatDaily;
import org.example.hansabal.domain.batch.service.ProductTradeStatBatchService;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ProductDailyWriter implements ItemWriter<ProductTradeStatDaily> {

	private final ProductTradeStatBatchService service;

	/**
	 * 배치 처리된 일별 주문 통계 아이템들을 저장하는 메서드
	 *
	 * @param items Spring Batch가 전달하는 청크 단위의 ProductStatDaily 리스트
	 */
	@Override
	public void write(Chunk<? extends ProductTradeStatDaily> items) {
		// ProductStatId 기준으로 같은 키를 가진 일별 통계 데이터 그룹화
		Map<ProductTradeStatId, List<ProductTradeStatDaily>> grouped = items.getItems().stream()
				.collect(Collectors.groupingBy(ProductTradeStatDaily::getId));

		// 그룹별로 주문 건수와 총 매출 합산 후, 새로운 ProductStatDaily 객체 생성
		List<ProductTradeStatDaily> merged = grouped.entrySet().stream()
				.map(e -> {
					int TradeCount = e.getValue().stream()
							.mapToInt(ProductTradeStatDaily::getTradeCount)
							.sum();
					long totalSales = e.getValue().stream()
							.mapToLong(ProductTradeStatDaily::getTotalSales)
							.sum();
					// 정적 팩토리 메서드 사용하여 새로운 객체 생성
					return ProductTradeStatDaily.of(e.getKey(), TradeCount, totalSales);
				})
				.toList();

		// 합산된 데이터를 서비스 레이어에 위임하여 저장 처리
		service.saveDaily(merged);
	}
}
