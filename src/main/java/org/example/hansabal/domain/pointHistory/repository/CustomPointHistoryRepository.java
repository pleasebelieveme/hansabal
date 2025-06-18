package org.example.hansabal.domain.pointHistory.repository;



import org.example.hansabal.domain.pointHistory.entity.PointHistory;
import org.example.hansabal.domain.pointHistory.entity.PointUseHistory;

import java.util.List;

public interface CustomPointHistoryRepository {
	List<PointHistory> findUnusedPointsOrderByCreatedDateAsc(Long userId);

	public List<PointUseHistory> applyUserPointUsage(Long userId, Integer point);
}
