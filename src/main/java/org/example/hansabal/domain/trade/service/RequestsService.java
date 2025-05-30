package org.example.hansabal.domain.trade.service;

import java.util.List;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.trade.dto.request.RequestsRequestDto;
import org.example.hansabal.domain.trade.dto.response.RequestsListResponseDto;
import org.example.hansabal.domain.trade.dto.response.RequestsResponseDto;
import org.example.hansabal.domain.trade.entity.Requests;
import org.example.hansabal.domain.trade.entity.Trade;
import org.example.hansabal.domain.trade.exception.TradeErrorCode;
import org.example.hansabal.domain.trade.repository.RequestsRepository;
import org.example.hansabal.domain.trade.repository.TradeRepository;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestsService {
	private final RequestsRepository requestsRepository;
	private final TradeRepository tradeRepository;
	private final UserRepository userRepository;

	public void createRequests(UserAuth userAuth, RequestsRequestDto request) {
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());
		Trade trade = tradeRepository.findById(request.tradeId()).orElseThrow(()-> new BizException(
			TradeErrorCode.NoSuchThing));
		Requests requests = Requests.of(trade,user);
		requestsRepository.save(requests);
	}

	public RequestsListResponseDto getRequestList(Long tradeId, Pageable pageable) {
		Page<Requests> page = requestsRepository.findAllByTradeIdOrderByRequestsIdDesc(tradeId, pageable);
		List<RequestsResponseDto> requestsList = page.getContent()
			.stream()
			.map(this::convertToDto)
			.toList();
		Long count = page.getTotalElements();
		return new RequestsListResponseDto(count, requestsList);
	}
	private RequestsResponseDto convertToDto(Requests requests) {
		return RequestsResponseDto.from(requests);
	}
}
