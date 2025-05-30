package org.example.hansabal.domain.trade.service;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.trade.dto.request.RequestsRequestDto;
import org.example.hansabal.domain.trade.entity.Requests;
import org.example.hansabal.domain.trade.entity.Trade;
import org.example.hansabal.domain.trade.exception.TradeErrorCode;
import org.example.hansabal.domain.trade.repository.RequestsRepository;
import org.example.hansabal.domain.trade.repository.TradeRepository;
import org.example.hansabal.domain.users.entity.User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestsService {
	private final RequestsRepository requestsRepository;
	private final TradeRepository tradeRepository;

	public void createRequests(User user, RequestsRequestDto request) {
		Trade trade = tradeRepository.findById(request.tradeId()).orElseThrow(()-> new BizException(
			TradeErrorCode.NoSuchThing));
		Requests requests = Requests.of(trade,user);
		requestsRepository.save(requests);
	}
}
