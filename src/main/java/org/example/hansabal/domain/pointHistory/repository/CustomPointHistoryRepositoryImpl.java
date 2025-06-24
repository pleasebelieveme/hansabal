package org.example.hansabal.domain.pointHistory.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.pointHistory.entity.PointHistory;
import org.example.hansabal.domain.pointHistory.entity.PointUseHistory;
import org.example.hansabal.domain.pointHistory.entity.PointUsed;
import org.example.hansabal.domain.pointHistory.entity.QPointHistory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomPointHistoryRepositoryImpl implements CustomPointHistoryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<PointHistory> findUnusedPointsTradeByCreatedDateAsc(Long userId) {
		QPointHistory qPointHistory = QPointHistory.pointHistory;

		return queryFactory
			.selectFrom(qPointHistory)
			.where(
				qPointHistory.user.id.eq(userId),
				qPointHistory.pointUsed.eq(PointUsed.UNUSED)
			)
			.orderBy(qPointHistory.createdAt.asc())
			.fetch();
	}

	@Override
	@Transactional
	public List<PointUseHistory> applyUserPointUsage(Long userId, Integer point) {
		QPointHistory q = QPointHistory.pointHistory;

		List<PointHistory> pointHistories = queryFactory
			.selectFrom(q)
			.where(q.user.id.eq(userId)
				.and(q.pointUsed.eq(PointUsed.UNUSED)))
			.orderBy(q.createdAt.asc())
			.fetch();

		List<PointUseHistory> usedHistories = new ArrayList<>();

		for (PointHistory ph : pointHistories) {
			if (point == 0)
				break;

			int remain = ph.getRemain();
			if (remain > point) {
				ph.updateRemain(remain - point);
				usedHistories.add(new PointUseHistory(ph, point));
				point = 0;
			} else {
				point -= remain;
				usedHistories.add(new PointUseHistory(ph, remain));
				ph.updateRemain(0);
			}
		}
		return usedHistories;
	}

}
