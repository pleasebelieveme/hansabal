package org.example.hansabal.domain.trade.service;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.trade.dto.request.RequestsRequestDto;
import org.example.hansabal.domain.trade.dto.request.RequestsStatusRequestDto;
import org.example.hansabal.domain.trade.dto.response.RequestsResponseDto;
import org.example.hansabal.domain.trade.entity.RequestStatus;
import org.example.hansabal.domain.trade.entity.Requests;
import org.example.hansabal.domain.trade.entity.Trade;
import org.example.hansabal.domain.trade.exception.TradeErrorCode;
import org.example.hansabal.domain.trade.repository.RequestsRepository;
import org.example.hansabal.domain.trade.repository.TradeRepository;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.repository.UserRepository;
//import org.example.hansabal.domain.wallet.service.WalletService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestsService {
	private final RequestsRepository requestsRepository;
	private final TradeRepository tradeRepository;
	private final UserRepository userRepository;
	//private final WalletService walletService;

	@Transactional
	public void createRequests(UserAuth userAuth, RequestsRequestDto request) {
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());
		Trade trade = tradeRepository.findById(request.tradeId()).orElseThrow(()-> new BizException(
			TradeErrorCode.TRADE_NOT_FOUND));
		Requests requests = Requests.of(trade,user);
		requestsRepository.save(requests);
	}

	@Transactional(readOnly=true)
	public Page<RequestsResponseDto> getRequestList(Long tradeId, int page, int size) {
		int pageIndex = Math.max(page - 1 , 0);
		Pageable pageable = PageRequest.of(pageIndex,size);
		Page<Requests> requests = requestsRepository.findByTradeIdOrderByRequestsIdAsc(tradeId, pageable);
		return requests.map(RequestsResponseDto::from);

	}

	@Transactional
	public void updateRequestsByTrader(Long requestsId, RequestsStatusRequestDto request, UserAuth userAuth) {
		Requests requests = requestsRepository.findById(requestsId).orElseThrow(()-> new BizException(TradeErrorCode.REQUESTS_NOT_FOUND));
		if(requests.getStatus()==RequestStatus.DONE)
			throw new BizException(TradeErrorCode.CLOSED_CASE);
		Trade trade = tradeRepository.findById(requests.getTrade().getId()).orElseThrow(()-> new BizException(TradeErrorCode.TRADE_NOT_FOUND));
		if(!trade.getTrader().getId().equals(userAuth.getId()))
			throw new BizException(TradeErrorCode.NOT_ALLOWED);
		if(trade.getIsOccupied()&&requests.getStatus()==RequestStatus.AVAILABLE)//이미 거래 요청을 받아들인 상태에서 다른 거래 요청을 추가로 받아들이는 것을 금지.
			throw new BizException(TradeErrorCode.ALREADY_OCCUPIED);
		if(requests.getStatus()==RequestStatus.AVAILABLE)//거래상태가 '가능'이고 가격이 무료가 아닐 때 배송단계로 넘기는것을 금지.
			if(request.requestStatus()==RequestStatus.SHIPPING&&trade.getPrice()!=0L)
				throw new BizException(TradeErrorCode.NOT_PAID);
		if(request.requestStatus()==RequestStatus.PAID||request.requestStatus()==RequestStatus.DONE)//거래 요청자가 지정해야할 상태로 변경 금지.
			throw new BizException(TradeErrorCode.NOT_SUPPORTED_TYPE);
		if(requests.getStatus()==RequestStatus.PAID&&request.requestStatus()==RequestStatus.PENDING)//지불 완료된 거래요청을 지불대기로 변경하는것을 방지
			throw new BizException(TradeErrorCode.NOT_SUPPORTED_TYPE);
		requests.updateStatus(request.requestStatus());
		if(!trade.getIsOccupied())
			trade.occupiedCheck(true);
	}

	@Transactional
	public void cancelRequests(Long requestsId, UserAuth userAuth) {
		Requests requests = requestsRepository.findById(requestsId).orElseThrow(()-> new BizException(TradeErrorCode.REQUESTS_NOT_FOUND));
		if(requests.getStatus()!= RequestStatus.AVAILABLE)
			throw new BizException(TradeErrorCode.NOT_IDLE_REQUESTS);
		Trade trade = tradeRepository.findById(requests.getTrade().getId()).orElseThrow(()-> new BizException(TradeErrorCode.TRADE_NOT_FOUND));
		if(!requests.getRequester().getId().equals(userAuth.getId()))
			throw new BizException(TradeErrorCode.UNAUTHORIZED);
		requests.softDelete();
	}

	@Transactional
	public void payTradeFee(Long requestsId, UserAuth userAuth) {
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());
		Requests requests = requestsRepository.findById(requestsId).orElseThrow(()-> new BizException(TradeErrorCode.REQUESTS_NOT_FOUND));
		Trade trade = tradeRepository.findById(requests.getTrade().getId()).orElseThrow(()-> new BizException(TradeErrorCode.TRADE_NOT_FOUND));
		if(!requests.getRequester().getId().equals(user.getId()))
			throw new BizException(TradeErrorCode.NOT_ALLOWED);
		if(requests.getStatus()!=RequestStatus.PENDING)
			throw new BizException(TradeErrorCode.WRONG_STAGE);
		//Long price = trade.getPrice();
		//walletService.walletPay(user, trade.getId(), price);
		requests.updateStatus(RequestStatus.PAID);
	}

	@Transactional
	public void confirmGoods(Long requestsId, UserAuth userAuth) {
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());
		Requests requests = requestsRepository.findById(requestsId).orElseThrow(()-> new BizException(TradeErrorCode.REQUESTS_NOT_FOUND));
		Trade trade = tradeRepository.findById(requests.getTrade().getId()).orElseThrow(()-> new BizException(TradeErrorCode.TRADE_NOT_FOUND));
		if(!requests.getRequester().getId().equals(user.getId()))
			throw new BizException(TradeErrorCode.NOT_ALLOWED);
		if(requests.getStatus()!=RequestStatus.SHIPPING)
			throw new BizException(TradeErrorCode.WRONG_STAGE);
		//walletService.walletConfirm(trade,requestsId);
		requests.updateStatus(RequestStatus.DONE);
		trade.softDelete();
	}
}
