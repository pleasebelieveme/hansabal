package org.example.hansabal.domain.trade.repository;

import org.example.hansabal.domain.trade.dto.response.TradeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TradeRepositoryCustom {
		Page<TradeResponseDto> findByTitleContainingAndDeletedAtIsNullTradeByIdDesc(String title, Pageable pageable);
		Page<TradeResponseDto> findByTraderTradeByTradeIdDesc(Long traderId, Pageable pageable);
}
