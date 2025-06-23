package org.example.hansabal.domain.trade.service;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.trade.dto.request.TradeRequest;
import org.example.hansabal.domain.trade.dto.response.TradeResponse;
import org.example.hansabal.domain.trade.entity.Trade;
import org.example.hansabal.domain.trade.exception.TradeErrorCode;
import org.example.hansabal.domain.trade.repository.TradeRepository;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TradeService {
	private final TradeRepository tradeRepository;
	private final UserRepository userRepository;

	@Transactional
	public TradeResponse createTrade(TradeRequest request, UserAuth userAuth) {
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());
		Trade trade= Trade.builder()
			.title(request.title())
			.contents(request.contents())
			.trader(user)
			.price(request.price())
			.isOccupied(false)
			.build();
		tradeRepository.save(trade);
		return TradeResponse.from(trade);
	}

	@Transactional(readOnly=true)
	public Page<TradeResponse> getTradeListByTitle(int page, int size, String title) {
		if(title==null)
			title="";
		int pageIndex = Math.max(page - 1 , 0);
		Pageable pageable = PageRequest.of(pageIndex,size);
		Page<TradeResponse> trades = tradeRepository.findByTitleContainingAndDeletedAtIsNullOrderByIdDesc(title,pageable);
		return trades;
	}

	@Transactional(readOnly=true)
	public TradeResponse getTrade(Long tradeId) {
		Trade trade = tradeRepository.findById(tradeId).orElseThrow(()-> new BizException(TradeErrorCode.TRADE_NOT_FOUND));
		return TradeResponse.from(trade);
	}

	@Transactional(readOnly=true)
	public Page<TradeResponse> getMyTrade(UserAuth userAuth, int page, int size) {
		int pageIndex = Math.max(page - 1 , 0);
		Pageable pageable = PageRequest.of(pageIndex,size);
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());
		Long traderId=user.getId();
		Page<TradeResponse> trades = tradeRepository.findByTraderOrderByTradeIdDesc(traderId,pageable);
		return trades;
	}

	@Transactional
	public TradeResponse updateTrade(Long tradeId, TradeRequest request, UserAuth userAuth) {
		Trade trade = tradeRepository.findById(tradeId).orElseThrow(()-> new BizException(TradeErrorCode.TRADE_NOT_FOUND));
		if(!trade.getTrader().getId().equals(userAuth.getId()))
			throw new BizException(TradeErrorCode.UNAUTHORIZED);
		trade.updateTrade(request.title(),request.contents(), request.price());
		return TradeResponse.from(trade);
	}

	@Transactional
	public void cancelTrade(Long tradeId, UserAuth userAuth) {
		Trade trade = tradeRepository.findById(tradeId).orElseThrow(()-> new BizException(TradeErrorCode.TRADE_NOT_FOUND));
		if(!trade.getTrader().getId().equals(userAuth.getId()))
			throw new BizException(TradeErrorCode.UNAUTHORIZED);
		trade.softDelete();
	}
}
