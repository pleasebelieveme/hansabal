package org.example.hansabal.domain.batch.reader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hansabal.domain.admin.entity.ProductTradeStatDaily;
import org.example.hansabal.domain.batch.repository.ProductTradeStatDailyRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProductTradeStatMonthlyReader implements ItemReader<ProductTradeStatDaily> {

	private final ProductTradeStatDailyRepository dailyRepository;

	// 일별 통계 데이터를 순차적으로 읽기 위한 Iterator
	private Iterator<ProductTradeStatDaily> dailyIterator;

	@Override
	public ProductTradeStatDaily read() {
		// 최초 호출 시에만 DB에서 해당 월 일별 통계 데이터를 조회
		if (dailyIterator == null) {
			LocalDate now = LocalDate.now();

			// 조회 기간 시작일: 어제 날짜를 기준으로 그 달의 1일로 설정 (ex: 2025-06-10 -> 2025-06-01)
			LocalDate from = now.minusDays(1).withDayOfMonth(1);

			// 조회 기간 종료일: 현재 달의 1일 (ex: 2025-06-10 -> 2025-06-01)
			LocalDate to = now.withDayOfMonth(1);

			// from (포함) ~ to (미포함) 기간의 일별 주문 통계 조회
			List<ProductTradeStatDaily> items = dailyRepository.findAllByDateRange(from, to);

			// 조회 결과를 Iterator로 변환하여 순차적으로 처리 준비
			dailyIterator = items.iterator();
		}
		// 데이터가 남아 있으면 다음 항목 반환, 없으면 null 반환 (배치 Step 종료 신호)
		return dailyIterator.hasNext() ? dailyIterator.next() : null;
	}
}
