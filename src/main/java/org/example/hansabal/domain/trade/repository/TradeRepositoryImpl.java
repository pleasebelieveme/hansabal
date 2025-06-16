package org.example.hansabal.domain.trade.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

public class TradeRepositoryImpl implements TradeRepositoryCustom{
	private final JPAQueryFactory queryFactory;

}
