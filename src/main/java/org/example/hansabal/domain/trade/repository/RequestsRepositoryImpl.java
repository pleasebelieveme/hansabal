package org.example.hansabal.domain.trade.repository;

import java.util.List;

import org.example.hansabal.domain.trade.dto.response.RequestsResponseDto;
import org.example.hansabal.domain.trade.entity.QRequests;
import org.example.hansabal.domain.trade.entity.Requests;
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
	public Page<RequestsResponseDto> findByTradeIdTradeByRequestsIdAsc(Long tradeId, Pageable pageable){
		QRequests requests = QRequests.requests;

		List<RequestsResponseDto> content = queryFactory
			.select(Projections.constructor(
				RequestsResponseDto.class,
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

		long total = queryFactory
			.select(requests.id)
			.from(requests)
			.where(requests.trade.id.eq(tradeId))
			.fetch()
			.size();

		return new PageImpl<>(content,pageable,total);
	}

}
