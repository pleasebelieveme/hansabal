package org.example.hansabal.domain.trade.controller;

import org.example.hansabal.common.security.CurrentUser;
import org.example.hansabal.domain.trade.dto.request.RequestesRequestDto;
import org.example.hansabal.domain.trade.dto.request.TradeRequestDto;
import org.example.hansabal.domain.trade.service.RequestesService;
import org.example.hansabal.domain.trade.service.TradeService;
import org.example.hansabal.domain.users.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trade")

public class TradeController {
	private final TradeService tradeService;
	private final RequestesService requestesService;

	@PostMapping
	public ResponseEntity<Void> createTrade(@RequestBody TradeRequestDto request, @CurrentUser User user) {
		tradeService.createTrade(request, user);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}


	@PostMapping
	public ResponseEntity<Void> createRequestes(@RequestBody RequestesRequestDto request, @CurrentUser User user){
		requestesService.createRequestes(request, user);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
