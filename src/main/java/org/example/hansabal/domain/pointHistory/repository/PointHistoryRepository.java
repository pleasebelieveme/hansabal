package org.example.hansabal.domain.pointHistory.repository;


import org.example.hansabal.domain.pointHistory.entity.PointHistory;
import org.example.hansabal.domain.pointHistory.entity.PointUsed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long>, CustomPointHistoryRepository {

	@Query("SELECT SUM(p.remain) FROM PointHistory p WHERE p.user.id = :userId AND p.pointUsed = :pointUsed")
	Integer sumRemainByUserIdAndPointUsed(@Param("userId") Long userId, @Param("pointUsed") PointUsed pointUsed);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE PointHistory p SET p.deletedAt= CURRENT_TIMESTAMP WHERE p.user.id = :userId")
	void deleteByUserId(@Param("userId") Long userId);
}
