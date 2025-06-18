package org.example.hansabal.domain.admin.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.example.hansabal.domain.batch.entity.AdminProductOrderStatDaily;
import org.example.hansabal.domain.batch.entity.QAdminProductOrderStatDaily;
import org.springframework.stereotype.Repository;

@Repository
public class AdminProductOrderStatDailyQueryRepositoryImpl
	extends AbstractAdminProductOrderStatQueryRepository<AdminProductOrderStatDaily, QAdminProductOrderStatDaily>
	implements AdminProductOrderStatDailyQueryRepository {

	public AdminProductOrderStatDailyQueryRepositoryImpl(JPAQueryFactory queryFactory) {
		super(
			queryFactory,
			QAdminProductOrderStatDaily.adminProductOrderStatDaily,
			QAdminProductOrderStatDaily.adminProductOrderStatDaily.date
		);
	}
}
