package org.example.hansabal.domain.batch.writer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hansabal.domain.admin.repository.AdminProductTradeStatMonthlyRepository;
import org.example.hansabal.domain.batch.entity.AdminProductTradeStatMonthly;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class AdminProductMonthlyWriter implements ItemWriter<AdminProductTradeStatMonthly> {

	private final AdminProductTradeStatMonthlyRepository monthlyRepository;

	/**
	 * 배치 처리에서 읽어온 AdminProductTradeStatMonthly 아이템들을 저장하는 메서드
	 *
	 * @param items Spring Batch에서 처리할 청크 단위의 아이템
	 */
	@Override
	public void write(Chunk<? extends AdminProductTradeStatMonthly> items) {
		// 월별 데이터 그룹화 (LocalDate는 통상적으로 월의 첫째 날로 관리)
		Map<LocalDate, List<AdminProductTradeStatMonthly>> grouped = items.getItems().stream()
				.collect(Collectors.groupingBy(AdminProductTradeStatMonthly::getDate));

		// 같은 월 데이터를 합산하여 하나의 AdminProductTradeStatMonthly 객체로 병합
		List<AdminProductTradeStatMonthly> merged = grouped.entrySet().stream()
				.map(e -> {
					// 해당 월의 주문 수 총합 계산
					int TradeCount = e.getValue().stream()
							.mapToInt(AdminProductTradeStatMonthly::getTradeCount)
							.sum();
					// 해당 월의 총 매출 합계 계산
					long totalSales = e.getValue().stream()
							.mapToLong(AdminProductTradeStatMonthly::getTotalSales)
							.sum();
					// 합산된 결과를 새로운 엔티티로 반환
					return new AdminProductTradeStatMonthly(e.getKey(), TradeCount, totalSales);
				})
				.toList();

		// 합산된 월별 통계 데이터를 한 번에 저장
		monthlyRepository.saveAll(merged);
	}
}
