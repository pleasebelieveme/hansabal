package org.example.hansabal.domain.trade.service;

import java.util.List;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.trade.dto.request.TradeRequestDto;
import org.example.hansabal.domain.trade.dto.response.TradeListResponseDto;
import org.example.hansabal.domain.trade.dto.response.TradeResponseDto;
import org.example.hansabal.domain.trade.entity.Trade;
import org.example.hansabal.domain.trade.exception.TradeErrorCode;
import org.example.hansabal.domain.trade.repository.TradeRepository;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TradeService {
	private final TradeRepository tradeRepository;
	private final UserRepository userRepository;

	public void createTrade(TradeRequestDto request, UserAuth userAuth) {
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());
		Trade trade= Trade.builder()
			.title(request.title())
			.contents(request.contents())
			.trader(user)
			.build();
		tradeRepository.save(trade);
	}

	public TradeListResponseDto getTradeList(Pageable pageable) {
		Page<Trade> page = tradeRepository.findAllByOrderByTradeIdDesc(pageable);
		List<TradeResponseDto> tradeList = page.getContent()
			.stream()
			.map(this::convertToDto)
			.toList();
		Long count = page.getTotalElements();
		return new TradeListResponseDto(count, tradeList);
	}
	private TradeResponseDto convertToDto(Trade trade) {
		return TradeResponseDto.from(trade);
	}

	public TradeResponseDto getTrade(Long tradeId) {
		Trade trade = tradeRepository.findById(tradeId).orElseThrow(()-> new BizException(TradeErrorCode.NoSuchThing));
		return TradeResponseDto.from(trade);
	}
}
