package org.example.hansabal.domain.wallet.service;

import java.util.List;

import jakarta.persistence.EntityManager;
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
	private final EntityManager em;

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
		Long updatedCash = wallet.getCash() - price;
		walletHistoryService.historySaver(wallet,tradeId,price,"구매",updatedCash);
		wallet.updateWallet(updatedCash);
	}

	@Transactional
	public void walletConfirm(Trade trade, Long requestsId) {

		log.info("➡️ walletConfirm 실행 시작: tradeId={}, requestsId={}", trade.getId(), requestsId);

		requestsRepository.findById(requestsId)
			.orElseThrow(() -> new BizException(WalletErrorCode.WRONG_REQUESTS_CONNECTED));

		User trader = trade.getTrader();

		Wallet wallet = walletRepository.findByUser(trader)
			.orElseThrow(() -> new BizException(WalletErrorCode.NO_WALLET_FOUND));


		List<WalletHistory> histories = walletHistoryRepository.findAllByTradeId(trade.getId());
		if (histories.isEmpty()) {
			throw new BizException(WalletErrorCode.HISTORY_NOT_EXIST);
		}

		// 조건에 맞는 가장 최신 기록 1건 (예: 가격 일치 && 판매 타입)
		WalletHistory matchedHistory = histories.stream()
			.filter(h -> h.getPrice().equals(trade.getPrice()) && "구매".equals(h.getType()))
			.findFirst()
			.orElseThrow(() -> new BizException(WalletErrorCode.DATA_MISMATCH));

		log.info("✅ matched walletHistory: {}", matchedHistory);

		Long updatedCash = wallet.getCash() + trade.getPrice();
		walletHistoryService.historySaver(wallet, trade.getId(), trade.getPrice() * (-1L), "판매수익",updatedCash);

		List<WalletHistory> allHistories = walletHistoryRepository.findAllByTradeId(trade.getId());
		log.info("✅ [DEBUG] 저장된 WalletHistory 개수: {}", allHistories.size());
		for (WalletHistory h : allHistories) {
			log.info("🔍 [DEBUG] historyId={}, tradeId={}, type={}, price={}, remain={}",
				h.getId(), h.getTradeId(), h.getType(), h.getPrice(), h.getRemain());
		}

		wallet.updateWallet(updatedCash);
		em.flush();
		em.clear();
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
