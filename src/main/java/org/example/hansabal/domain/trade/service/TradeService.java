package org.example.hansabal.domain.trade.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.example.hansabal.domain.trade.dto.request.TradeRequestDto;
import org.example.hansabal.domain.trade.dto.response.TradeResponseDto;
import org.example.hansabal.domain.trade.entity.Trade;
import org.example.hansabal.domain.trade.repository.TradeRepository;
import org.example.hansabal.domain.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TradeService {
	private final TradeRepository tradeRepository;

	public void createTrade(TradeRequestDto request, User user) {
		Trade trade= Trade.builder()
			.title(request.title())
			.contents(request.contents())
			.trader(user)
			.build();
		tradeRepository.save(trade);
	}

	public List<TradeResponseDto> getTrades(Pageable pageable) {
		Page<Trade> page = tradeRepository.findAllOrderByTradeIdDesc(pageable);
	}
}
