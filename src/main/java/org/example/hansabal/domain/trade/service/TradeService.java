package org.example.hansabal.domain.trade.service;

import org.example.hansabal.domain.trade.dto.request.TradeRequestDto;
import org.example.hansabal.domain.trade.entity.Trade;
import org.example.hansabal.domain.trade.repository.TradeRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TradeService {
	private final TradeRepository tradeRepository;

	public void createTrade(TradeRequestDto request) {
		Trade trade= Trade.builder()
			.title(request.title())
			.contents(request.contents())
			.trader(request.trader()
		).build();
		tradeRepository.save(trade);
	}
}
