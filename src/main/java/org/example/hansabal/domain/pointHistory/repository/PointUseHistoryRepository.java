package org.example.hansabal.domain.pointHistory.repository;

import org.example.hansabal.domain.pointHistory.entity.PointUseHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointUseHistoryRepository extends JpaRepository<PointUseHistory, Long> {
}
