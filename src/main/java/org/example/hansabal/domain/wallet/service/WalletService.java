package org.example.hansabal.domain.wallet.service;

import lombok.extern.slf4j.Slf4j;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.payment.entity.Payment;
import org.example.hansabal.domain.payment.entity.PaymentStatus;
import org.example.hansabal.domain.payment.repository.PaymentRepository;
import org.example.hansabal.domain.trade.entity.Trade;
import org.example.hansabal.domain.trade.repository.RequestsRepository;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.example.hansabal.domain.wallet.dto.request.LoadRequest;
import org.example.hansabal.domain.wallet.dto.response.WalletResponse;
import org.example.hansabal.domain.wallet.entity.Wallet;
import org.example.hansabal.domain.wallet.entity.WalletHistory;
import org.example.hansabal.domain.wallet.exception.WalletErrorCode;
import org.example.hansabal.domain.wallet.repository.WalletHistoryRepository;
import org.example.hansabal.domain.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

	private final UserRepository userRepository;
	private final WalletRepository walletRepository;
	private final RequestsRepository requestsRepository;
	private final WalletHistoryService walletHistoryService;
	private final WalletHistoryRepository walletHistoryRepository;
	private final PaymentRepository paymentRepository;

	@Transactional
	public void createWallet(User user) {

		if(walletRepository.existsByUser(user))
			throw new BizException(WalletErrorCode.DUPLICATE_WALLET_NOT_ALLOWED);
		Wallet wallet =  Wallet.builder()
			.user(user)
			.cash(0L)
			.build();
		walletRepository.save(wallet);
	}

	@Transactional
	public Payment loadWallet(LoadRequest request) {
		Payment payment = Payment.builder()
			.price(request.cash())
			.status(PaymentStatus.READY)
			.build();
		paymentRepository.save(payment);
		return payment;
	}

	@Transactional(propagation= Propagation.REQUIRES_NEW)
	public void walletPay(User user, Long tradeId, Long price){//trade 에서 비용 지불시 사용(거래 상태 PAID 으로 바꿀 때 작동)
		Wallet wallet = walletRepository.findByUser(user).orElseThrow(()->new BizException(WalletErrorCode.NO_WALLET_FOUND));
		if(wallet.getCash()<price)
			throw new BizException(WalletErrorCode.NOT_ENOUGH_CASH);
		walletHistoryService.historySaver(wallet,tradeId,price,"구매");
		wallet.updateWallet(wallet.getCash()-price);
	}

	@Transactional(propagation= Propagation.REQUIRES_NEW)
	public void walletConfirm(Trade trade, Long requestsId) {//trade 에서 거래 물품 확인시 사용(거래상태 DONE 으로 바꿀 때 작동)
		requestsRepository.findById(requestsId).orElseThrow(()->new BizException(WalletErrorCode.WRONG_REQUESTS_CONNECTED));
		User trader= trade.getTrader();
		Wallet wallet = walletRepository.findByUser(trader).orElseThrow(()->new BizException(WalletErrorCode.NO_WALLET_FOUND));
		WalletHistory walletHistory = walletHistoryRepository.findByTradeId(trade.getId());
		if(walletHistory==null)
			throw new BizException(WalletErrorCode.HISTORY_NOT_EXIST);
		if(!walletHistory.getPrice().equals(trade.getPrice()))
			throw new BizException(WalletErrorCode.DATA_MISMATCH);
		walletHistoryService.historySaver(wallet,trade.getId(),trade.getPrice()*(-1L),"판매수익");
		wallet.updateWallet(wallet.getCash()+trade.getPrice());

	}

	@Transactional(readOnly=true)
	public WalletResponse getWallet(UserAuth userAuth) {
		log.info("✅ getWallet 진입");
		try {
			User user = userRepository.findByIdOrElseThrow(userAuth.getId());
			Wallet wallet = walletRepository.findByUser(user)
					.orElseThrow(() -> new BizException(WalletErrorCode.NO_WALLET_FOUND));

			log.info("💳 walletgetId : {}, userName : {}, walletcash : {}", wallet.getId(), user.getName(), wallet.getCash());
			log.info("🔎 userId 확인: {}", user.getId());
			log.info("🔎 지갑 존재 여부: {}", true);

			return new WalletResponse(wallet.getId(), user.getName(), wallet.getCash());
		} catch (Exception e) {
			log.error("❌ getWallet 내부에서 예외 발생", e);
			throw new BizException(WalletErrorCode.INTERNAL_SERVICE_ERROR);
		}

	}
}
