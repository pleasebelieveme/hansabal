package org.example.hansabal.domain.trade.repository;

import org.example.hansabal.domain.trade.dto.response.TradeResponseDto;
import org.example.hansabal.domain.trade.entity.Trade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TradeRepositoryCustom {
		Page<TradeResponseDto> findByTitleContainingAndDeletedAtIsNullOrderByIdDesc(String title, Pageable pageable);
		Page<TradeResponseDto> findByTraderOrderByTradeIdDesc(Long traderId, Pageable pageable);
}
