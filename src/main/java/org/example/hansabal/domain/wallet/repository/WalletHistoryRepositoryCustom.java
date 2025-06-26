package org.example.hansabal.domain.wallet.repository;

import org.example.hansabal.domain.wallet.dto.response.HistoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WalletHistoryRepositoryCustom {
	Page<HistoryResponse> findByWalletIdOrderByCreatedAtDesc(Pageable pageable,Long walletId);
}
