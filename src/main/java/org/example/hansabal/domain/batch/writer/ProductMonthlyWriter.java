package org.example.hansabal.domain.batch.writer;


import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.admin.entity.ProductOrderStatId;
import org.example.hansabal.domain.admin.entity.ProductOrderStatMonthly;
import org.example.hansabal.domain.batch.repository.ProductOrderStatMonthlyRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ProductMonthlyWriter implements ItemWriter<ProductOrderStatMonthly> {

	private final ProductOrderStatMonthlyRepository monthlyRepository;

	/**
	 * 월별 주문 통계 데이터를 저장하는 메서드
	 *
	 * @param items Spring Batch가 전달하는 청크 단위의 ProductStatMonthly 리스트
	 */
	@Override
	public void write(Chunk<? extends ProductOrderStatMonthly> items) {
		// 같은 ProductStatId를 가진 항목들이 여러 개 있을 경우 합산하여 하나의 객체로 병합
		Map<ProductOrderStatId, ProductOrderStatMonthly> grouped = items.getItems().stream()
				.collect(Collectors.toMap(
						ProductOrderStatMonthly::getId,
						stat -> stat,
						(s1, s2) -> {
							// 중복된 키의 경우 orderCount와 totalSales를 합산
							s1.add(s2.getOrderCount(), s2.getTotalSales());
							return s1;
						}
				));

		// 병합된 결과를 리스트로 변환
		List<ProductOrderStatMonthly> merged = grouped.values().stream().toList();

		// 병합된 데이터를 한 번에 저장
		monthlyRepository.saveAll(merged);
	}
}
