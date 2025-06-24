package org.example.hansabal.domain.trade.service;


import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.product.repository.ProductRepository;
import org.example.hansabal.domain.trade.dto.request.TradeRequestDto;
import org.example.hansabal.domain.trade.dto.request.TradeStatusRequestDto;
import org.example.hansabal.domain.trade.dto.response.TradeDetailResponseDto;
import org.example.hansabal.domain.trade.dto.response.TradeItemDetailResponseDto;
import org.example.hansabal.domain.trade.dto.response.TradeResponseDto;
import org.example.hansabal.domain.trade.entity.RequestStatus;
import org.example.hansabal.domain.trade.entity.Trade;
import org.example.hansabal.domain.trade.entity.TradeItem;
import org.example.hansabal.domain.trade.entity.TradeStatus;
import org.example.hansabal.domain.trade.exception.TradeErrorCode;
import org.example.hansabal.domain.trade.repository.TradeItemRepository;
import org.example.hansabal.domain.trade.repository.TradeRepository;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.exception.UserErrorCode;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradeService {

	private final ProductRepository productRepository;
	private final TradeItemService tradeItemService;
	private final TradeItemRepository tradeItemRepository;
	private final TradeRepository tradeRepository;
	private final UserRepository userRepository;

	@Transactional
	public void createTrade(TradeRequestDto request, UserAuth userAuth) {
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());

		Trade trade = Trade.builder()
				.title(request.title())
				.contents(request.contents())
				.writer(user)
				.price(request.price())
				.status(TradeStatus.valueOf(RequestStatus.AVAILABLE.name()))
				.isOccupied(false)
				.build();

		tradeRepository.save(trade);
	}

	@Transactional
	public TradeResponseDto updateTradeStatus(Long tradeId, UserAuth userAuth, TradeStatusRequestDto requestDto) {
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());
		Trade trade = tradeRepository.findById(tradeId)
				.orElseThrow(() -> new BizException(TradeErrorCode.TRADE_NOT_FOUND));

		if (!trade.getWriter().getId().equals(user.getId())) {
			throw new BizException(TradeErrorCode.UNAUTHORIZED);
		}

		RequestStatus currentStatus = RequestStatus.valueOf(trade.getStatus().name());
		RequestStatus requestedStatus = requestDto.requestStatus();

		if (!currentStatus.equals(requestedStatus)) {
			throw new BizException(TradeErrorCode.INVALID_TRADE_STATUS);
		}

		switch (currentStatus) {
			case AVAILABLE -> trade.updateStatus(RequestStatus.PENDING);
			case PENDING -> trade.updateStatus(RequestStatus.PAID);
			case PAID -> trade.updateStatus(RequestStatus.SHIPPING);
			case SHIPPING -> trade.updateStatus(RequestStatus.DONE);
			case DONE -> throw new BizException(TradeErrorCode.ALREADY_COMPLETED);
			default -> throw new BizException(TradeErrorCode.INVALID_TRADE_STATUS);
		}

		return TradeResponseDto.from(trade);
	}

	@Transactional(readOnly = true)
	public Page<TradeResponseDto> getTradesByUser(Long userId, int page, int size) {
		User user = userRepository.findByIdOrElseThrow(userId);
		Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

		Page<Trade> trades;
		if (user.getUserRole() == UserRole.ADMIN) {
			trades = tradeRepository.findAll(pageable);
		} else {
			trades = tradeRepository.findByWriter(user, pageable);
		}

		return trades.map(TradeResponseDto::from);
	}

	@Transactional(readOnly = true)
	public TradeResponseDto getTradeDetail(Long tradeId, Long userId) {
		User user = userRepository.findByIdOrElseThrow(userId);
		Trade trade = tradeRepository.findById(tradeId)
				.orElseThrow(() -> new BizException(TradeErrorCode.TRADE_NOT_FOUND));

		if (!Objects.equals(trade.getWriter().getId(), user.getId())) {
			throw new BizException(TradeErrorCode.UNAUTHORIZED);
		}

		return TradeResponseDto.from(trade);
	}

	@Transactional
	public void updateTrade(Long tradeId, TradeRequestDto request, UserAuth userAuth) {
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());
		Trade trade = tradeRepository.findById(tradeId)
				.orElseThrow(() -> new BizException(TradeErrorCode.TRADE_NOT_FOUND));

		if (!trade.getWriter().getId().equals(user.getId())) {
			throw new BizException(TradeErrorCode.UNAUTHORIZED);
		}

		trade.updateTrade(request.title(), request.contents(), request.price());
	}

	@Transactional
	public void cancelTrade(Long tradeId, UserAuth userAuth) {
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());
		Trade trade = tradeRepository.findById(tradeId)
				.orElseThrow(() -> new BizException(TradeErrorCode.TRADE_NOT_FOUND));

		if (!trade.getWriter().getId().equals(user.getId())) {
			throw new BizException(TradeErrorCode.UNAUTHORIZED);
		}

		// 소프트 삭제 또는 상태 완료 처리
		trade.updateStatus(RequestStatus.DONE);
	}

	/**
	 * 주문 생성 로직
	 * - 쿠폰 적용
	 * - 포인트 차감
	 * - 총 금액 계산
	 * - 주문 저장 및 주문 항목 저장
	 * - 장바구니 제거
	 */
	/**
	 * 쿠폰 유형에 따라 총 금액 할인 적용
	 */

	/**
	 * 사장님이 주문 상태를 변경하는 로직
	 * - CHECKING → COOKING → DELIVERING → FINISHED 순으로 상태 전이
	 * - 최종 완료 시 포인트 적립
	 */
	@Transactional
	public TradeResponseDto updateTrade(Long userId, Long TradeId, @Valid TradeStatusRequestDto requestDto) {
		User user = validateUser(userId);
		Trade trade = validateTrade(user, TradeId);

		if (!trade.getStatus().equals(requestDto.tradeStatus())) {
			throw new BizException(TradeErrorCode.INVALID_TRADE_STATUS);
		}

		if (trade.getStatus() == TradeStatus.CHECKING) {
			trade.updateStatus(TradeStatus.COOKING);
		} else if (trade.getStatus() == TradeStatus.COOKING) {
			trade.updateStatus(TradeStatus.DELIVERING);
		} else if (trade.getStatus() == TradeStatus.DELIVERING) {
			trade.updateStatus(TradeStatus.FINISHED);
		}

		List<Long> menuIds = tradeItemService.getTradeItemIds(TradeId);

		return new TradeResponseDto(
				trade.getId(),
				trade.getTitle(),
				trade.getContents(),
				trade.getWriter().getId(),
				trade.getWriter().getNickname()
		);
	}

	/**
	 * 주문 소유자(사장) 확인 및 주문 상태 검증
	 */
	private Trade validateTrade(User user, Long TradeId) {
		Trade trade = tradeRepository.findById(TradeId)
				.orElseThrow(() -> new BizException(TradeErrorCode.TRADE_NOT_FOUND));

		if (!Objects.equals(trade.getProduct().getUser().getId(), user.getId())) {
			throw new BizException(TradeErrorCode.PRODUCT_OWNER_MISMATCH);
		}

		if (trade.getStatus() == TradeStatus.FINISHED) {
			throw new BizException(TradeErrorCode.Trade_ALREADY_FINISHED);
		}

		return trade;
	}

	/**
	 * 사용자 유효성 검증
	 */
	private User validateUser(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new BizException(UserErrorCode.INVALID_REQUEST));
	}

	/**
	 * 주문 거절 처리 (CHECKING 상태에서만 가능)
	 */
	@Transactional
	public void refuseTrade(Long userId, Long TradeId) {
		User user = validateUser(userId);
		Trade Trade = validateTrade(user, TradeId);

		if (!Trade.getStatus().equals(TradeStatus.CHECKING)) {
			throw new BizException(TradeErrorCode.NOT_SUPPORTED_TYPE);
		}

		Trade.updateStatus(TradeStatus.REFUSED);
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
