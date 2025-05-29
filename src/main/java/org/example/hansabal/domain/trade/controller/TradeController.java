package org.example.hansabal.domain.trade.controller;

import java.util.List;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.security.CurrentUser;
import org.example.hansabal.domain.trade.dto.request.RequestsRequestDto;
import org.example.hansabal.domain.trade.dto.request.TradeRequestDto;
import org.example.hansabal.domain.trade.dto.response.TradeResponseDto;
import org.example.hansabal.domain.trade.entity.Trade;
import org.example.hansabal.domain.trade.exception.TradeErrorCode;
import org.example.hansabal.domain.trade.repository.TradeRepository;
import org.example.hansabal.domain.trade.service.RequestsService;
import org.example.hansabal.domain.trade.service.TradeService;
import org.example.hansabal.domain.users.entity.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trade")

public class TradeController {
	private final TradeService tradeService;
	private final TradeRepository tradeRepository;
	private final RequestsService requestsService;

	@PostMapping
	public ResponseEntity<Void> createTrade(@RequestBody TradeRequestDto request, @CurrentUser User user) {
		tradeService.createTrade(request, user);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping
	public ResponseEntity<List<TradeResponseDto>> getTrades(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
		Pageable pageable = PageRequest.of(page, size);
		List<TradeResponseDto> tradeList = tradeService.getTrades(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(tradeList);
	}

	@PostMapping("/requestes")
	public ResponseEntity<Void> createRequests(@RequestBody RequestsRequestDto request, @CurrentUser User user){
		Trade trade = tradeRepository.findById(request.tradeId()).orElseThrow(()-> new BizException(
			TradeErrorCode.NoSuchThing));
		requestsService.createRequests(user, trade);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
