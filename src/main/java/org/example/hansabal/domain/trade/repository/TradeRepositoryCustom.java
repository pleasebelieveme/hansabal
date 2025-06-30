package org.example.hansabal.domain.trade.repository;

import org.example.hansabal.domain.trade.dto.response.TradeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TradeRepositoryCustom {
	Page<TradeResponse> findByDeletedAtIsNullOrderByIdDesc(Pageable pageable);
	Page<TradeResponse> findByTitleContainingAndDeletedAtIsNullOrderByIdDesc(String title, Pageable pageable);
		Page<TradeResponse> findByTraderOrderByTradeIdDesc(Long traderId, Pageable pageable);
}
