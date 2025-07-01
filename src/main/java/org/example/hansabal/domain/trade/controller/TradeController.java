package org.example.hansabal.domain.trade.controller;

import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.trade.dto.request.RequestsRequest;
import org.example.hansabal.domain.trade.dto.request.RequestsStatusRequest;
import org.example.hansabal.domain.trade.dto.request.TradeRequest;
import org.example.hansabal.domain.trade.dto.response.RequestsResponse;
import org.example.hansabal.domain.trade.dto.response.TradeResponse;
import org.example.hansabal.domain.trade.service.RequestsService;
import org.example.hansabal.domain.trade.service.TradeService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trade")

public class TradeController {
	private final TradeService tradeService;
	private final RequestsService requestsService;

	@PostMapping
	public ResponseEntity<TradeResponse> createTrade(@Valid @RequestBody TradeRequest request, @AuthenticationPrincipal UserAuth userAuth) {
		TradeResponse response = tradeService.createTrade(request, userAuth);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping// full-scan 발생 주의보, full-text-index 및 커스텀 함수 처리 후 개선 예정
	public ResponseEntity<Page<TradeResponse>> getTradesByTitle(@RequestParam(defaultValue = "1") @Positive int page, @RequestParam(defaultValue = "10") @Positive int size, @RequestParam(required=false, value="title") String title){
		Page<TradeResponse> tradeList = tradeService.getTradeListByTitle(page, size, title);
		return ResponseEntity.status(HttpStatus.OK).body(tradeList);
	}

	@GetMapping("/{tradeId}")
	public ResponseEntity<TradeResponse> getTrade(@PathVariable Long tradeId){
		TradeResponse response = tradeService.getTrade(tradeId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/my")// full-scan 발생 주의보, full-text-index 및 커스텀 함수 처리 후 개선 예정
	public ResponseEntity<Page<TradeResponse>> getMyTrades(
		@RequestParam(defaultValue="1") @Positive int page, @RequestParam(defaultValue="10") @Positive int size, @AuthenticationPrincipal UserAuth userAuth){
		Page<TradeResponse> myTradeList = tradeService.getMyTrade(page,size,userAuth);
		return ResponseEntity.status(HttpStatus.OK).body(myTradeList);
	}

	@PatchMapping("/{tradeId}")
	public ResponseEntity<TradeResponse> updateTrade(@PathVariable Long tradeId, @Valid @RequestBody TradeRequest request, @AuthenticationPrincipal UserAuth userAuth){
		TradeResponse response = tradeService.updateTrade(tradeId, request, userAuth);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@DeleteMapping("/{tradeId}")
	public ResponseEntity<Void> cancelTrade(@PathVariable Long tradeId, @AuthenticationPrincipal UserAuth userAuth){
		tradeService.cancelTrade(tradeId, userAuth);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PostMapping("/requests")
	public ResponseEntity<RequestsResponse> createRequests(@Valid @RequestBody RequestsRequest request, @AuthenticationPrincipal UserAuth userAuth){
		RequestsResponse response = requestsService.createRequests(userAuth, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{tradeId}/requests")
	public ResponseEntity<Page<RequestsResponse>> getRequests(@PathVariable Long tradeId, @RequestParam(defaultValue="1") @Positive int page, @RequestParam(defaultValue="10") @Positive int size){
		Page<RequestsResponse> requestsList = requestsService.getRequestList(tradeId, page, size);
		return ResponseEntity.status(HttpStatus.OK).body(requestsList);

	}

	@PatchMapping("/requests/{requestsId}")
	public ResponseEntity<RequestsResponse> updateRequests(@PathVariable Long requestsId, @Valid @RequestBody RequestsStatusRequest request, @AuthenticationPrincipal UserAuth userAuth){
		RequestsResponse response = requestsService.updateRequestsByTrader(requestsId, request, userAuth);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@DeleteMapping("/requests/{requestsId}")
	public ResponseEntity<Void> cancelRequests(@PathVariable Long requestsId, @AuthenticationPrincipal UserAuth userAuth){
		requestsService.cancelRequests(requestsId, userAuth);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PatchMapping("/requests/{requestsId}/pay")
	public ResponseEntity<RequestsResponse> payTradeFee(@PathVariable Long requestsId, @AuthenticationPrincipal UserAuth userAuth){
		RequestsResponse response = requestsService.payTradeFee(requestsId, userAuth);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PatchMapping("/requests/{requestsId}/confirm")
	public ResponseEntity<RequestsResponse> confirmGoods(@PathVariable Long requestsId, @AuthenticationPrincipal UserAuth userAuth){
		RequestsResponse response = requestsService.confirmGoods(requestsId, userAuth);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
