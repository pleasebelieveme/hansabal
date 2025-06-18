package org.example.hansabal.domain.admin.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.example.hansabal.domain.batch.entity.AdminProductOrderStatMonthly;
import org.example.hansabal.domain.batch.entity.QAdminProductOrderStatMonthly;
import org.springframework.stereotype.Repository;

@Repository
public class AdminProductOrderStatMonthlyQueryRepositoryImpl
	extends AbstractAdminProductOrderStatQueryRepository<AdminProductOrderStatMonthly, QAdminProductOrderStatMonthly>
	implements AdminProductOrderStatMonthlyQueryRepository {

	public AdminProductOrderStatMonthlyQueryRepositoryImpl(JPAQueryFactory queryFactory) {
		super(
			queryFactory,
			QAdminProductOrderStatMonthly.adminProductOrderStatMonthly,
			QAdminProductOrderStatMonthly.adminProductOrderStatMonthly.date
		);
	}
}
