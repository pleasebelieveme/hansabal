package org.example.hansabal.domain.wallet.repository;

import org.example.hansabal.domain.wallet.entity.Wallet;
import org.example.hansabal.domain.wallet.entity.WalletHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletHistoryRepository extends JpaRepository<WalletHistory, Long> {
	@EntityGraph(attributePaths="wallet")
	@Query(value="SELECT h FROM WalletHistory h WHERE h.walletId=:walletId ORDER BY h.createdAt desc",
		countQuery ="SELECT h FROM WalletHistory h WHERE h.walletId=:walletId")
	Page<WalletHistory> findByWalletIdOrderByCreatedAtDesc(Pageable pageable,@Param("walletId") Wallet wallet);

	@Query("Select h From WalletHistory h join fetch Wallet w Where h.tradeId=:tradeId")
	WalletHistory findByTradeId(@Param("tradeId")Long tradeId);
}
