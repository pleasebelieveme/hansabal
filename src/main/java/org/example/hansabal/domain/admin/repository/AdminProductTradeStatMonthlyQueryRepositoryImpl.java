package org.example.hansabal.domain.admin.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.example.hansabal.domain.batch.entity.AdminProductTradeStatMonthly;
import org.example.hansabal.domain.batch.entity.QAdminProductTradeStatMonthly;
import org.springframework.stereotype.Repository;

@Repository
public class AdminProductTradeStatMonthlyQueryRepositoryImpl
	extends AbstractAdminProductTradeStatQueryRepository<AdminProductTradeStatMonthly, QAdminProductTradeStatMonthly>
	implements AdminProductTradeStatMonthlyQueryRepository {

	public AdminProductTradeStatMonthlyQueryRepositoryImpl(JPAQueryFactory queryFactory) {
		super(
			queryFactory,
			QAdminProductTradeStatMonthly.adminProductTradeStatMonthly,
			QAdminProductTradeStatMonthly.adminProductTradeStatMonthly.date
		);
	}
}
