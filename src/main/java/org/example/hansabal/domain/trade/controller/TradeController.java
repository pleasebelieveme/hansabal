package org.example.hansabal.domain.trade.controller;

import org.example.hansabal.domain.trade.dto.request.TradeRequestDto;
import org.example.hansabal.domain.trade.service.TradeService;
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

	@PostMapping
	public ResponseEntity<Void> createTrade(@RequestBody TradeRequestDto request) {
		tradeService.createTrade(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
