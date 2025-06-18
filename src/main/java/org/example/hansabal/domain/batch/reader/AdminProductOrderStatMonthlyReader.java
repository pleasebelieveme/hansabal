package org.example.hansabal.domain.batch.reader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hansabal.domain.admin.entity.ProductOrderStatMonthly;
import org.example.hansabal.domain.batch.repository.ProductOrderStatMonthlyRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class AdminProductOrderStatMonthlyReader implements ItemReader<ProductOrderStatMonthly> {

	private final ProductOrderStatMonthlyRepository monthlyRepository;

	// 한번에 불러온 월별 통계 데이터를 순차적으로 반환하기 위한 Iterator
	private Iterator<ProductOrderStatMonthly> monthlyIterator;

	@Override
	public ProductOrderStatMonthly read() {
		// 처음 read() 호출 시에만 DB에서 데이터를 조회
		if (monthlyIterator == null) {
			// 처리 대상: 바로 이전 달 (예: 현재 6월이면 5월 데이터 조회)
			LocalDate targetDate = LocalDate.now().minusMonths(1);

			// Repository에서 targetDate에 해당하는 월별 통계 데이터를 조회
			List<ProductOrderStatMonthly> stats = monthlyRepository.findAllByDate(targetDate);

			// 조회된 결과를 Iterator로 변환
			monthlyIterator = stats.iterator();
		}

		// 데이터가 남아있으면 다음 객체 반환, 없으면 null 반환 (Step 종료 신호)
		return monthlyIterator.hasNext() ? monthlyIterator.next() : null;
	}
}
