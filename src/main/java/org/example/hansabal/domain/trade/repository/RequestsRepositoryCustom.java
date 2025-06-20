package org.example.hansabal.domain.trade.repository;

import org.example.hansabal.domain.trade.dto.response.RequestsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RequestsRepositoryCustom {
		Page<RequestsResponse> findByTradeIdOrderByRequestsIdAsc(Long tradeId, Pageable pageable);
}
