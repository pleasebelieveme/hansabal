package org.example.hansabal.domain.wallet.repository;
import java.util.List;
import java.util.Optional;

import org.example.hansabal.domain.wallet.dto.response.HistoryResponse;
import org.example.hansabal.domain.wallet.entity.QWalletHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WalletHistoryRepositoryImpl implements WalletHistoryRepositoryCustom{
	private final JPAQueryFactory queryFactory;
	@Override
	public Page<HistoryResponse> findByWalletIdOrderByCreatedAtDesc(Pageable pageable, Long walletId){
		QWalletHistory walletHistory = QWalletHistory.walletHistory;

		List<HistoryResponse> content = queryFactory
			.select(Projections.constructor(
				HistoryResponse.class,
				walletHistory.tradeId,
				walletHistory.type,
				walletHistory.price,
				walletHistory.remain
			))
			.from(walletHistory)
			.where(walletHistory.wallet.id.eq(walletId).and(walletHistory.wallet.deletedAt.isNull()))
			.orderBy(walletHistory.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
		Long total = Optional.ofNullable(queryFactory
			.select(walletHistory.id)
			.from(walletHistory)
			.where(walletHistory.wallet.id.eq(walletId).and(walletHistory.wallet.deletedAt.isNull()))
			.fetchOne()
		).orElse(0L);

		return new PageImpl<>(content,pageable,total);
	}
}
