package org.example.hansabal.domain.batch.reader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hansabal.domain.admin.entity.ProductOrderStatDaily;
import org.example.hansabal.domain.admin.entity.ProductStatDaily;
import org.example.hansabal.domain.batch.repository.ProductOrderStatDailyRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class AdminProductOrderStatDailyReader implements ItemReader<ProductStatDaily> {

	// ✅ Repository는 ProductOrderStatDaily 기준인데, 반환 타입은 ProductStatDaily → 불일치 주의!
	private final ProductOrderStatDailyRepository dailyRepository;

	// ✅ JPA에서 조회한 결과를 순차적으로 반환할 Iterator
	private Iterator<ProductStatDaily> dailyIterator;

	@Override
	public ProductStatDaily read() {
		// ✅ 첫 read() 호출일 경우만 DB 조회 수행
		if (dailyIterator == null) {
			LocalDate to = LocalDate.now();         // 오늘 날짜
			LocalDate from = to.minusDays(1);       // 어제 날짜


			// ✅ 해결 방법: Repository 혹은 Reader 둘 중 하나의 엔티티 기준을 일치시켜야 함
			List<ProductStatDaily> stats = dailyRepository.findAllByDateRange(from, to);

			// ✅ iterator에 담아 이후 호출부터 순차적으로 반환
			dailyIterator = stats.iterator();
		}

		// ✅ 데이터가 있으면 하나씩 반환, 없으면 null로 Step 종료 유도
		return dailyIterator.hasNext() ? dailyIterator.next() : null;
	}
}
