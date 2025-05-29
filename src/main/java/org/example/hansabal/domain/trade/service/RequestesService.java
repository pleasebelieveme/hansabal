package org.example.hansabal.domain.trade.service;

import org.example.hansabal.domain.trade.dto.request.RequestesRequestDto;
import org.example.hansabal.domain.trade.entity.Requestes;
import org.example.hansabal.domain.trade.repository.RequestesRepository;
import org.example.hansabal.domain.users.entity.User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestesService {
	private final RequestesRepository requestesRepository;

	public void createRequestes(RequestesRequestDto request, User user) {
		Requestes requestes = Requestes.of(request.tradeId(),user);
		requestesRepository.save(requestes);
	}
}
