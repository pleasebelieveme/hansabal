package org.example.hansabal.domain.wallet.controller;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.payment.entity.Payment;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.example.hansabal.domain.wallet.dto.request.LoadRequestDto;
import org.example.hansabal.domain.wallet.dto.response.HistoryResponseDto;
import org.example.hansabal.domain.wallet.dto.response.WalletResponseDto;
import org.example.hansabal.domain.wallet.entity.Wallet;
import org.example.hansabal.domain.wallet.exception.WalletErrorCode;
import org.example.hansabal.domain.wallet.repository.WalletRepository;
import org.example.hansabal.domain.wallet.service.WalletHistoryService;
import org.example.hansabal.domain.wallet.service.WalletService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
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
	private final UserRepository userRepository;
	private final WalletRepository walletRepository;

	@PostMapping()
	public ResponseEntity<Void> createWallet(@AuthenticationPrincipal UserAuth userAuth){
		walletService.createWallet(userAuth);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/load")//프론트로 전송 data 전송 및 리디렉션
	public String loadWallet(@RequestBody LoadRequestDto request, @AuthenticationPrincipal UserAuth userAuth){
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());
		Wallet wallet = walletRepository.findById(request.id()).orElseThrow(()->new BizException(WalletErrorCode.NO_WALLET_FOUND));
		Payment payment = walletService.loadWallet(request, wallet);
		String uuid = walletHistoryService.historyChargeSaver(wallet, request.cash(), payment);
		return "redirect:/api/wallet/load?uuid="+uuid;
	}

	@GetMapping("/load")
	public String load(@RequestParam(name = "uuid", required = false) String uuid, Model model) {

		model.addAttribute("uuid", uuid);

		return "payment";
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
