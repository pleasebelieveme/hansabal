package org.example.hansabal.domain.trade.repository;

import java.util.List;
import java.util.Optional;

import org.example.hansabal.domain.trade.dto.response.RequestsResponse;
import org.example.hansabal.domain.trade.entity.QRequests;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

public class RequestsRepositoryImpl implements RequestsRepositoryCustom{
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<RequestsResponse> findByTradeIdOrderByRequestsIdAsc(Long tradeId, Pageable pageable){
		QRequests requests = QRequests.requests;

		List<RequestsResponse> content = queryFactory
			.select(Projections.constructor(
				RequestsResponse.class,
				requests.id,
				requests.status,
				requests.trade,
				requests.requester
			))
			.from(requests)
			.where(requests.trade.id.eq(tradeId).and(requests.deletedAt.isNull()))
			.orderBy(requests.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = Optional.ofNullable(queryFactory
			.select(requests.id)
			.from(requests)
			.where(requests.trade.id.eq(tradeId))
			.fetchOne()
		).orElse(0L);

		return new PageImpl<>(content,pageable,total);
	}

}
