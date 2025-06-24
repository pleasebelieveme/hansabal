package org.example.hansabal.domain.batch.reader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.example.hansabal.domain.trade.entity.Trade;
import org.example.hansabal.domain.trade.entity.TradeStatus;
import org.example.hansabal.domain.trade.repository.TradeRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProductTradeStatDailyReader implements ItemReader<Trade> {

	private final TradeRepository tradeRepository;

	// 한번에 읽어온 주문 리스트를 순차적으로 반환하기 위한 Iterator
	private Iterator<Trade> tradeIterator;

	@Override
	public Trade read() {
		// 처음 호출 시에만 DB에서 주문 데이터를 조회
		if (tradeIterator == null) {
			// 배치 처리 대상 날짜: 하루 전 날짜(어제)
			LocalDate targetDate = LocalDate.now().minusDays(1);
			// 하루 전 날짜의 시작 시각 (00:00:00)
			LocalDateTime from = targetDate.atStartOfDay();
			// 하루 전 날짜의 다음 날 시작 시각 (익일 00:00:00)
			LocalDateTime to = targetDate.plusDays(1).atStartOfDay();

			// 완료된 주문 상태(TradeStatus.FINISHED)이며, 생성일자가 from~to 사이인 주문들을 조회
			List<Trade> trades = tradeRepository.findAllByStatusAndCreatedDateRange(TradeStatus.FINISHED, from, to);

			// 조회한 주문 목록을 Iterator로 변환
			tradeIterator = trades.iterator();
		}
		// 주문이 남아있으면 다음 주문을 반환하고, 모두 소진되면 null 반환하여 Step 종료 신호 전달
		return tradeIterator.hasNext() ?tradeIterator.next() : null;
	}
}
