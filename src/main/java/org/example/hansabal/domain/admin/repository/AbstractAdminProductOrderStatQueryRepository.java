	package org.example.hansabal.domain.admin.repository;

	import com.querydsl.core.types.dsl.DatePath;
	import com.querydsl.core.types.dsl.EntityPathBase;
	import com.querydsl.jpa.impl.JPAQueryFactory;

	import java.time.LocalDate;
	import java.util.List;

	public abstract class AbstractAdminProductOrderStatQueryRepository<T, Q extends EntityPathBase<T>> {

		protected final JPAQueryFactory queryFactory;
		protected final Q q;
		protected final DatePath<LocalDate> datePath;

		public AbstractAdminProductOrderStatQueryRepository(JPAQueryFactory queryFactory, Q q, DatePath<LocalDate> datePath) {
			this.queryFactory = queryFactory;
			this.q = q;
			this.datePath = datePath;
		}

		public List<T> findAllByDateRange(LocalDate from, LocalDate to) {
			return queryFactory
				.selectFrom(q)
				.where(
					datePath.goe(from),
					datePath.lt(to)
				)
				.fetch();
		}

	}
