package org.example.hansabal.domain.batch.writer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hansabal.domain.admin.repository.AdminProductTradeStatDailyRepository;
import org.example.hansabal.domain.batch.entity.AdminProductTradeStatDaily;
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
public class AdminProductDailyWriter implements ItemWriter<AdminProductTradeStatDaily> {

	private final AdminProductTradeStatDailyRepository dailyRepository;

	/**
	 * 배치 처리에서 읽어온 AdminProductTradeStatDaily 아이템들을 저장하는 메서드
	 *
	 * @param items Spring Batch에서 처리할 청크 단위의 아이템
	 */
	@Override
	public void write(Chunk<? extends AdminProductTradeStatDaily> items) {
		// 날짜별로 AdminProductTradeStatDaily 객체들을 그룹화
		Map<LocalDate, List<AdminProductTradeStatDaily>> grouped = items.getItems().stream()
				.collect(Collectors.groupingBy(AdminProductTradeStatDaily::getDate));

		// 같은 날짜 데이터를 합산하여 새로운 리스트 생성
		List<AdminProductTradeStatDaily> merged = grouped.entrySet().stream()
				.map(e -> {
					// 해당 날짜의 주문 수 총합
					int TradeCount = e.getValue().stream()
							.mapToInt(AdminProductTradeStatDaily::getTradeCount)
							.sum();
					// 해당 날짜의 총 매출 합계
					long totalSales = e.getValue().stream()
							.mapToLong(AdminProductTradeStatDaily::getTotalSales)
							.sum();
					// 합산된 결과를 새로운 엔티티로 생성하여 반환
					return new AdminProductTradeStatDaily(e.getKey(), TradeCount, totalSales);
				})
				.toList();

		// 합산된 데이터를 모두 저장
		dailyRepository.saveAll(merged);
	}
}
