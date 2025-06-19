package org.example.hansabal.domain.trade.repository;

import java.util.List;

import org.example.hansabal.domain.trade.dto.response.TradeResponseDto;
import org.example.hansabal.domain.trade.entity.QTrade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

public class TradeRepositoryImpl implements TradeRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override//.containing은 커스텀 함수 처리 예정
	public Page<TradeResponseDto> findByTitleContainingAndDeletedAtIsNullOrderByIdDesc(String title, Pageable pageable) {
		QTrade trade = QTrade.trade;

		List<TradeResponseDto> content = queryFactory
			.select(Projections.constructor(
				TradeResponseDto.class,
				trade.id,
				trade.title,
				trade.contents,
				trade.trader.id,
				trade.trader.nickname
			))
			.from(trade)
			.where(trade.title.like(title).and(trade.deletedAt.isNull()))//이후 커스텀함수로 containing도입 예정
			.orderBy(trade.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = queryFactory
			.select(trade.id)
			.from(trade)
			.where(trade.title.like(title))//이후 커스텀함수로 containing도입 예정
			.fetch()
			.size();

		return new PageImpl<>(content, pageable, total);

	}

	@Override
	public Page<TradeResponseDto> findByTraderOrderByTradeIdDesc(Long tradeId, Pageable pageable) {
		QTrade trade = QTrade.trade;

		List<TradeResponseDto> content = queryFactory
			.select(Projections.constructor(
				TradeResponseDto.class,
				trade.id,
				trade.title,
				trade.contents,
				trade.trader.id,
				trade.trader.nickname
			))
			.from(trade)
			.where(trade.id.eq(tradeId).and(trade.deletedAt.isNull()))
			.orderBy(trade.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = queryFactory
			.select(trade.id)
			.from(trade)
			.where(trade.id.eq(tradeId))
			.fetch()
			.size();

		return new PageImpl<>(content, pageable, total);
	}
}
