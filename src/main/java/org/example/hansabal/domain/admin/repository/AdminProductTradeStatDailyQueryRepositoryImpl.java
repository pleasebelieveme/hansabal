package org.example.hansabal.domain.admin.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.example.hansabal.domain.batch.entity.AdminProductTradeStatDaily;
import org.example.hansabal.domain.batch.entity.QAdminProductTradeStatDaily;
import org.springframework.stereotype.Repository;

@Repository
public class AdminProductTradeStatDailyQueryRepositoryImpl
	extends AbstractAdminProductTradeStatQueryRepository<AdminProductTradeStatDaily, QAdminProductTradeStatDaily>
	implements AdminProductTradeStatDailyQueryRepository {

	public AdminProductTradeStatDailyQueryRepositoryImpl(JPAQueryFactory queryFactory) {
		super(
			queryFactory,
			QAdminProductTradeStatDaily.adminProductTradeStatDaily,
			QAdminProductTradeStatDaily.adminProductTradeStatDaily.date
		);
	}
}
