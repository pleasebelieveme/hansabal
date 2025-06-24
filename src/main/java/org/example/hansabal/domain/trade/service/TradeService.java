package org.example.hansabal.domain.trade.service;


import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.trade.dto.request.TradeRequestDto;
import org.example.hansabal.domain.trade.dto.response.TradeResponseDto;
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

	/**
	 * 사용자 역할에 따른 주문 전체 조회
	 * - 일반 사용자: 자신이 주문한 주문
	 * - 사장님: 자신이 등록한 가게의 주문
	 */
	public List<org.example.hansabal.domain.trade.response.TradeResponseDto> findAllTrades(Long userId) {
		User user = validateUser(userId);
		List<Trade> trades = new ArrayList<>();

		if (user.getUserRole().equals(UserRole.USER)) {
			trades = tradeRepository.findByUser(user);
		} else if (user.getUserRole().equals(UserRole.ADMIN)) {
			List<Long> productIds = productRepository.findIdByUser(user);
			trades = tradeRepository.findByProductIds(productIds);
		}

		return trades.stream()
				.map(trade -> new org.example.hansabal.domain.trade.response.TradeResponseDto(
						trade.getId(),
						trade.getUser().getId(),
						trade.getProduct().getId(),
						tradeItemService.getTradeItemIds(trade.getId()),
						trade.getTotalPrice(),
						trade.getStatus()
				))
				.collect(Collectors.toList());
	}

	/**
	 * 특정 주문의 상세 정보 조회
	 * - 주문 항목(Menu, Option 등) 포함
	 * - 주문자가 직접 접근하거나 사장님이 접근 가능
	 */
	@Transactional
	public TradeDetailResponseDto findTradeDetails(Long userId, Long tradeId) {
		User user = validateUser(userId);
		Trade trade = validateDetailTrade(user, tradeId);

		List<TradeItem> tradeItems = tradeItemRepository.findByTradeId(tradeId);

		List<TradeItemDetailResponseDto> tradeItemDtos = tradeItems.stream()
				.map(tradeItem -> new TradeItemDetailResponseDto(
						tradeItem.getProduct().getId(),
						tradeItem.getProduct().getName(),
						tradeItem.getProduct() != null ?
								List.of() : List.of(),
						tradeItem.getQuantity()
				))
				.toList();

		return new TradeDetailResponseDto(
				trade.getId(),
				trade.getUser().getId(),
				trade.getProduct().getId(),
				tradeItemDtos,
				trade.getTotalPrice(),
				trade.getStatus(),
				trade.getCreatedAt()
		);
	}

	/**
	 * 주문 상세 조회 접근 권한 확인
	 */
	private Trade validateDetailTrade(User user, Long TradeId) {
		Trade trade = tradeRepository.findById(TradeId)
				.orElseThrow(() -> new BizException(TradeErrorCode.TRADE_NOT_FOUND));

		if (trade.getUser().getId().equals(user.getId()) ||
				trade.getProduct().getUser().getId().equals(user.getId())) {
			return trade;
		}

		throw new BizException(TradeErrorCode.NOT_ALLOWED);
	}

	@Transactional(readOnly = true)
	public Page<TradeResponseDto> getTradeListByTitle(int page, int size, String title) {
		Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
		Page<Trade> trades = tradeRepository.findByTitleContainingIgnoreCase(title, pageable);
		return trades.map(TradeResponseDto::from);
	}
	@Transactional(readOnly = true)
	public TradeResponseDto getTrade(Long tradeId) {
		Trade trade = tradeRepository.findById(tradeId)
				.orElseThrow(() -> new BizException(TradeErrorCode.TRADE_NOT_FOUND));
		return TradeResponseDto.from(trade);
	}

	@Transactional(readOnly = true)
	public Page<TradeResponseDto> getMyTrade(UserAuth userAuth, int page, int size) {
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());
		Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
		Page<Trade> trades = tradeRepository.findByWriter(user, pageable);
		return trades.map(TradeResponseDto::from);
	}

}
