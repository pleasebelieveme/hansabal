package org.example.hansabal.domain.trade.repository;

import org.example.hansabal.domain.trade.entity.Trade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TradeRepositoryCustom {
	//	Page<Trade> findByTitleContainingAndDeletedAtIsNullOrderByIdDesc(String title, Pageable pageable);
	//	Page<Trade> findByTraderOrderByTradeIdDesc(Long traderId, Pageable pageable);
}
