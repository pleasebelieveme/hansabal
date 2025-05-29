package org.example.hansabal.domain.trade.service;

import org.example.hansabal.domain.trade.entity.Requests;
import org.example.hansabal.domain.trade.entity.Trade;
import org.example.hansabal.domain.trade.repository.RequestsRepository;
import org.example.hansabal.domain.users.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestsService {
	private final RequestsRepository requestsRepository;

	public void createRequests(User user,Trade trade) throws ResponseStatusException {
		Requests requests = Requests.of(trade,user);
		requestsRepository.save(requests);
	}
}
