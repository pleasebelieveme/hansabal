package org.example.hansabal.domain.trade.controller;

import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.trade.dto.request.RequestsRequestDto;
import org.example.hansabal.domain.trade.dto.request.RequestsStatusRequestDto;
import org.example.hansabal.domain.trade.dto.request.TradeRequestDto;
import org.example.hansabal.domain.trade.dto.response.RequestsResponseDto;
import org.example.hansabal.domain.trade.dto.response.TradeResponseDto;
import org.example.hansabal.domain.trade.service.RequestsService;
import org.example.hansabal.domain.trade.service.TradeService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
	public ResponseEntity<Void> createTrade(@RequestBody TradeRequestDto request, @AuthenticationPrincipal UserAuth userAuth) {
		tradeService.createTrade(request, userAuth);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping
	public ResponseEntity<Page<TradeResponseDto>> getTrades(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size){
		Page<TradeResponseDto> tradeList = tradeService.getTradeList(page, size);
		return ResponseEntity.status(HttpStatus.OK).body(tradeList);
	}

	@GetMapping("/{tradeId}")
	public ResponseEntity<TradeResponseDto> getTrade(@PathVariable Long tradeId){
		TradeResponseDto response = tradeService.getTrade(tradeId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/my")
	public ResponseEntity<Page<TradeResponseDto>> getMyTrades(
		@RequestParam(defaultValue="1")int page, @RequestParam(defaultValue="10")int size, @AuthenticationPrincipal UserAuth userAuth){
		Page<TradeResponseDto> myTradeList = tradeService.getMyTrade(userAuth, page, size);
		return ResponseEntity.status(HttpStatus.OK).body(myTradeList);
	}

	@PatchMapping("/{tradeId}")
	public ResponseEntity<Void> updateTrade(@PathVariable Long tradeId, @RequestBody TradeRequestDto request, @AuthenticationPrincipal UserAuth userAuth){
		tradeService.updateTrade(tradeId, request, userAuth);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PostMapping("/requests")
	public ResponseEntity<Void> createRequests(@RequestBody RequestsRequestDto request, @AuthenticationPrincipal UserAuth userAuth){
		requestsService.createRequests(userAuth, request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping("/{tradeId}/requests")
	public ResponseEntity<Page<RequestsResponseDto>> getRequests(@PathVariable Long tradeId, @RequestParam(defaultValue="1") int page, @RequestParam(defaultValue="10")int size){
		Page<RequestsResponseDto> requestsList = requestsService.getRequestList(tradeId, page, size);
		return ResponseEntity.status(HttpStatus.OK).body(requestsList);

	}

	@PatchMapping("/requests/{requestsId}")
	public ResponseEntity<Void> updateRequests(@PathVariable Long requestsId, @RequestBody RequestsStatusRequestDto request, @AuthenticationPrincipal UserAuth userAuth){
		requestsService.updateRequests(requestsId, request, userAuth);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
