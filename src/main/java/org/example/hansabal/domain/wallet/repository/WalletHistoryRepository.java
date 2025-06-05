package org.example.hansabal.domain.wallet.repository;

import org.example.hansabal.domain.wallet.entity.WalletHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletHistoryRepository extends JpaRepository<WalletHistory, Long> {
}
