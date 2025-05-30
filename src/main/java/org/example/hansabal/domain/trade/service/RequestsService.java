package org.example.hansabal.domain.trade.service;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.trade.dto.request.RequestsRequestDto;
import org.example.hansabal.domain.trade.dto.response.RequestsResponseDto;
import org.example.hansabal.domain.trade.entity.Requests;
import org.example.hansabal.domain.trade.entity.Trade;
import org.example.hansabal.domain.trade.exception.TradeErrorCode;
import org.example.hansabal.domain.trade.repository.RequestsRepository;
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
public class RequestsService {
	private final RequestsRepository requestsRepository;
	private final TradeRepository tradeRepository;
	private final UserRepository userRepository;

	@Transactional
	public void createRequests(UserAuth userAuth, RequestsRequestDto request) {
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());
		Trade trade = tradeRepository.findById(request.tradeId()).orElseThrow(()-> new BizException(
			TradeErrorCode.NoSuchThing));
		Requests requests = Requests.of(trade,user);
		requestsRepository.save(requests);
	}

	@Transactional(readOnly=true)
	public Page<RequestsResponseDto> getRequestList(Long tradeId, int page, int size) {
		int pageIndex = Math.max(page - 1 , 0);
		Pageable pageable = PageRequest.of(pageIndex,size);
		Page<Requests> requests = requestsRepository.findAllByTradeIdOrderByRequestsIdAsc(tradeId, pageable);
		return requests.map(RequestsResponseDto::from);

	}

}
