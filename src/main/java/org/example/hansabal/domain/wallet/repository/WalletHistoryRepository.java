package org.example.hansabal.domain.wallet.repository;

import java.util.List;

import org.example.hansabal.domain.wallet.entity.WalletHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletHistoryRepository extends JpaRepository<WalletHistory, Long>, WalletHistoryRepositoryCustom {
	@Query("SELECT h FROM WalletHistory h JOIN FETCH h.wallet w LEFT JOIN FETCH h.payment " +
		"WHERE h.deletedAt IS NULL AND w.deletedAt IS NULL AND h.tradeId = :tradeId " +
		"ORDER BY h.createdAt DESC")
	List<WalletHistory> findAllByTradeId(@Param("tradeId") Long tradeId);

	@Query("Select h From WalletHistory h join fetch h.wallet join fetch h.payment Where h.deletedAt IS null AND h.uuid=:uuid")
	WalletHistory findByUuid(@Param("uuid")String uuid);
}
