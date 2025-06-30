package org.example.hansabal.domain.trade.repository;

import java.util.List;
import java.util.Optional;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.trade.dto.response.TradeResponse;
import org.example.hansabal.domain.trade.entity.QTrade;
import org.example.hansabal.domain.trade.exception.TradeErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor

public class TradeRepositoryImpl implements TradeRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	private BooleanExpression nameContaining(String query) {//nameContaining 정의
		QTrade trade = QTrade.trade;
		if (query == null) {
			log.error(TradeErrorCode.NO_SEARCH_QUERY.getMessage());
			throw new BizException(TradeErrorCode.NO_SEARCH_QUERY);
		}

		return Expressions.booleanTemplate("fulltext_match({0}, {1})", trade.title, query);
	}

	@Override
	public Page<TradeResponse> findByDeletedAtIsNullOrderByIdDesc(Pageable pageable) {
		QTrade trade = QTrade.trade;
		List<TradeResponse> content;
		content = queryFactory
				.select(Projections.constructor(
						TradeResponse.class,
						trade.title,
						trade.contents,
						trade.trader.nickname
				))
				.from(trade)
				.where(trade.deletedAt.isNull())
				.orderBy(trade.id.desc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		Long total = Optional.ofNullable(queryFactory
				.select(trade.count())
				.from(trade)
				.where(trade.deletedAt.isNull())
				.fetchOne()
		).orElse(0L);

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public Page<TradeResponse> findByTitleContainingAndDeletedAtIsNullOrderByIdDesc(String title, Pageable pageable) {
		QTrade trade = QTrade.trade;
		List<TradeResponse> content;
		content = queryFactory
				.select(Projections.constructor(
						TradeResponse.class,
						trade.id,
						trade.title,
						trade.contents,
						trade.trader.id,
						trade.price,
						trade.trader.nickname
				))
				.from(trade)
				.where(nameContaining(title).and(trade.deletedAt.isNull()))
				.orderBy(trade.id.desc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		Long total = Optional.ofNullable(queryFactory
				.select(trade.count())
				.from(trade)
				.where(nameContaining(title).and(trade.deletedAt.isNull()))
				.fetchOne()
		).orElse(0L);

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public Page<TradeResponse> findByTraderOrderByTradeIdDesc(Long tradeId, Pageable pageable) {
		QTrade trade = QTrade.trade;

		List<TradeResponse> content = queryFactory
				.select(Projections.constructor(
						TradeResponse.class,
						trade.title,
						trade.contents,
						trade.trader.nickname
				))
				.from(trade)
				.where(trade.id.eq(tradeId).and(trade.deletedAt.isNull()))
				.orderBy(trade.id.desc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		Long total = Optional.ofNullable(queryFactory
				.select(trade.count())
				.from(trade)
				.where(trade.id.eq(tradeId).and(trade.deletedAt.isNull()))
				.fetchOne()
		).orElse(0L);

		return new PageImpl<>(content, pageable, total);
	}
}