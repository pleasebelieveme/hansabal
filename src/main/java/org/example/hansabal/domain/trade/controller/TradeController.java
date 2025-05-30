package org.example.hansabal.domain.trade.controller;

import org.example.hansabal.common.security.CurrentUser;
import org.example.hansabal.domain.trade.dto.request.RequestsRequestDto;
import org.example.hansabal.domain.trade.dto.request.TradeRequestDto;
import org.example.hansabal.domain.trade.dto.response.RequestsListResponseDto;
import org.example.hansabal.domain.trade.dto.response.TradeListResponseDto;
import org.example.hansabal.domain.trade.dto.response.TradeResponseDto;
import org.example.hansabal.domain.trade.service.RequestsService;
import org.example.hansabal.domain.trade.service.TradeService;
import org.example.hansabal.domain.users.entity.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	private final RequestsService requestsService;

	@PostMapping
	public ResponseEntity<Void> createTrade(@RequestBody TradeRequestDto request, @CurrentUser User user) {
		tradeService.createTrade(request, user);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping
	public ResponseEntity<TradeListResponseDto> getTrades(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
		Pageable pageable = PageRequest.of(page, size);
		TradeListResponseDto tradeList = tradeService.getTradeList(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(tradeList);
	}

	@GetMapping("/{tradeId}")
	public ResponseEntity<TradeResponseDto> getTrade(@PathVariable Long tradeId){
		TradeResponseDto response = tradeService.getTrade(tradeId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/requests")
	public ResponseEntity<Void> createRequests(@RequestBody RequestsRequestDto request, @CurrentUser User user){

		requestsService.createRequests(user, request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping("/{tradeId}/requests")
	public ResponseEntity<RequestsListResponseDto> getRequests(@PathVariable Long tradeId, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10")int size){
		Pageable pageable = PageRequest.of(page,size);
		RequestsListResponseDto requestsList = requestsService.getRequestList(tradeId, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(requestsList);

	}
}
