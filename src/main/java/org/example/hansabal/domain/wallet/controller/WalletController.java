package org.example.hansabal.domain.wallet.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.payment.entity.Payment;
import org.example.hansabal.domain.wallet.dto.request.LoadRequest;
import org.example.hansabal.domain.wallet.dto.response.HistoryResponse;
import org.example.hansabal.domain.wallet.dto.response.WalletResponse;
import org.example.hansabal.domain.wallet.entity.Wallet;
import org.example.hansabal.domain.wallet.exception.WalletErrorCode;
import org.example.hansabal.domain.wallet.repository.WalletRepository;
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

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallet")
public class WalletController {

	private final WalletService walletService;
	private final WalletHistoryService walletHistoryService;
	private final WalletRepository walletRepository;

	// @PostMapping("/new")
	// public ResponseEntity<Void> createWallet(@AuthenticationPrincipal UserAuth userAuth){
	// 	walletService.createWallet(userAuth);
	// 	return ResponseEntity.status(HttpStatus.CREATED).build();
	// }
	@GetMapping("/wallet")
	public String walletPage() {
		return "wallet";  // resources/templates/wallet.html 로 렌더링됨
	}

	@PostMapping("/load")//프론트로 전송 data 전송 및 리디렉션, header로 바로이동
	public ResponseEntity<?> loadWallet(@RequestBody LoadRequest request, @AuthenticationPrincipal UserAuth userAuth){
		if (userAuth == null) {
			throw new BizException(WalletErrorCode.NO_WALLET_FOUND);
		}

		log.info("💳 LoadWallet 요청: userId={}, amount={}", userAuth.getId(), request.cash());

		WalletResponse response = walletService.getWallet(userAuth);
		Wallet wallet = walletRepository.findById(response.id())
				.orElseThrow(() -> new BizException(WalletErrorCode.NO_WALLET_FOUND));
		Payment payment = walletService.loadWallet(request);
		String uuid = walletHistoryService.historyLoadSaver(wallet, request.cash(), payment);

		Map<String, String> result = Map.of(
				"redirectUrl", "/payment?uuid=" + uuid + "&cash=" + request.cash()
		);
		return ResponseEntity.ok(result); // ✅ JSON으로 응답

	}

	// @PostMapping("/load2")//프론트로 전송 data 전송 및 리디렉션, header로 바로이동
	// public ResponseEntity<?> loadWalletC2(@RequestBody LoadRequest request, @AuthenticationPrincipal UserAuth userAuth){
	// 	if (userAuth == null) {
	// 		throw new BizException(WalletErrorCode.NO_WALLET_FOUND); // or custom AuthErrorCode
	// 	}
	//
	// 	log.info("💳 LoadWallet 요청: userId={}, amount={}", userAuth.getId(), request.cash());
	//
	// 	WalletResponse response = walletService.getWallet(userAuth);
	// 	Wallet wallet = walletRepository.findById(response.id()).orElseThrow(()->new BizException(WalletErrorCode.NO_WALLET_FOUND));
	// 	Payment payment = walletService.loadWallet(request);
	// 	String uuid = walletHistoryService.historyLoadSaver(wallet, request.cash(), payment);
	// 	HttpHeaders headers = new HttpHeaders();
	// 	headers.add("Location","/api/payment?uuid="+uuid);
	// 	return new ResponseEntity<Void>(headers,HttpStatus.FOUND);
	// }
	//
	// @PostMapping("/load3")//프론트로 전송 data 전송 및 리디렉션, servletRespone로 바로이동
	// public void loadWalletC3(@RequestBody LoadRequest request, @AuthenticationPrincipal UserAuth userAuth, HttpServletResponse response) {
	// 	if (userAuth == null) {
	// 		throw new BizException(WalletErrorCode.NO_WALLET_FOUND); // or custom AuthErrorCode
	// 	}
	//
	// 	log.info("💳 LoadWallet 요청: userId={}, amount={}", userAuth.getId(), request.cash());
	//
	// 	WalletResponse Wresponse = walletService.getWallet(userAuth);
	// 	Wallet wallet = walletRepository.findById(Wresponse.id()).orElseThrow(()->new BizException(WalletErrorCode.NO_WALLET_FOUND));
	// 	Payment payment = walletService.loadWallet(request);
	// 	String uuid = walletHistoryService.historyLoadSaver(wallet, request.cash(), payment);
	// 	try{response.sendRedirect("/api/payment?uuid=" + uuid);
	// 	}catch(IOException e){
	// 		throw new BizException(WalletErrorCode.IOEXCEPTION_FOUND);}
	// }

	@GetMapping()
	public ResponseEntity<WalletResponse> getWallet(@AuthenticationPrincipal UserAuth userAuth) {
		WalletResponse response = walletService.getWallet(userAuth);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/History")
	public ResponseEntity<Page<HistoryResponse>> getHistory(@RequestParam(defaultValue="1") @Positive int page,
		@RequestParam(defaultValue="10") @Positive int size,@AuthenticationPrincipal UserAuth userAuth){
		Page<HistoryResponse> response = walletHistoryService.getHistory(page, size, userAuth);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@GetMapping("/test")
	public ResponseEntity<String> test() {
		return ResponseEntity.ok("WalletController mapping OK!");
	}
}
