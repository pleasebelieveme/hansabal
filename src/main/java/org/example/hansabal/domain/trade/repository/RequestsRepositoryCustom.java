package org.example.hansabal.domain.trade.repository;

import org.example.hansabal.domain.trade.dto.response.RequestsResponseDto;
import org.example.hansabal.domain.trade.entity.Requests;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RequestsRepositoryCustom {
		Page<RequestsResponseDto> findByTradeIdTradeByRequestsIdAsc(Long tradeId, Pageable pageable);
}
