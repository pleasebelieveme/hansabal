package org.example.hansabal.domain.wallet.controller;

import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.wallet.dto.request.ChargeRequestDto;
import org.example.hansabal.domain.wallet.dto.response.HistoryResponseDto;
import org.example.hansabal.domain.wallet.dto.response.WalletResponseDto;
import org.example.hansabal.domain.wallet.service.WalletHistoryService;
import org.example.hansabal.domain.wallet.service.WalletService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallet")
public class WalletController {

	private final WalletService walletService;
	private final WalletHistoryService walletHistoryService;

	@PostMapping()
	public ResponseEntity<Void> createWallet(@AuthenticationPrincipal UserAuth userAuth){
		walletService.createWallet(userAuth);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/charge")
	public ResponseEntity<WalletResponseDto> chargeWallet(@RequestBody ChargeRequestDto request, @AuthenticationPrincipal UserAuth userAuth){
		WalletResponseDto response = walletService.chargeWallet(request, userAuth);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping()
	public ResponseEntity<WalletResponseDto> getWallet(@AuthenticationPrincipal UserAuth userAuth) {
		WalletResponseDto response = walletService.getWallet(userAuth);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/History")
	public ResponseEntity<Page<HistoryResponseDto>> getHistory(@RequestParam(defaultValue="1") @Positive int page,
		@RequestParam(defaultValue="10") @Positive int size,@AuthenticationPrincipal UserAuth userAuth){
		Page<HistoryResponseDto> response = walletHistoryService.getHistory(page, size, userAuth);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
