package org.example.hansabal.domain.wallet.service;

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

		if(walletRepository.existsByUserId(user))
			throw new BizException(WalletErrorCode.DUPLICATE_WALLET_NOT_ALLOWED);
		Wallet wallet =  Wallet.builder()
			.userId(user)
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
		// wallet.updateWallet(wallet.getCash()+request.cash()); 확인 후 충전으로 이동 예정
		return payment;
	}

	@Transactional(propagation= Propagation.REQUIRES_NEW)
	public void walletPay(User user, Long tradeId, Long price){//trade 에서 비용 지불시 사용(거래 상태 PAID 으로 바꿀 때 작동)
		Wallet wallet = walletRepository.findByUserId(user).orElseThrow(()->new BizException(WalletErrorCode.NO_WALLET_FOUND));
		if(wallet.getCash()<price)
			throw new BizException(WalletErrorCode.NOT_ENOUGH_CASH);
		wallet.updateWallet(wallet.getCash()-price);
		walletHistoryService.historySaver(wallet,tradeId,price,"구매");
	}

	@Transactional(propagation= Propagation.REQUIRES_NEW)
	public void walletConfirm(Trade trade, Long requestsId) {//trade 에서 거래 물품 확인시 사용(거래상태 DONE 으로 바꿀 때 작동)
		requestsRepository.findById(requestsId).orElseThrow(()->new BizException(WalletErrorCode.WRONG_REQUESTS_CONNECTED));
		User trader= trade.getTrader();
		Wallet wallet = walletRepository.findByUserId(trader).orElseThrow(()->new BizException(WalletErrorCode.NO_WALLET_FOUND));
		WalletHistory walletHistory = walletHistoryRepository.findByTradeId(trade.getId());
		if(walletHistory==null)
			throw new BizException(WalletErrorCode.HISTORY_NOT_EXIST);
		if(!walletHistory.getPrice().equals(trade.getPrice()))
			throw new BizException(WalletErrorCode.DATA_MISMATCH);
		wallet.updateWallet(wallet.getCash()+trade.getPrice());
		walletHistoryService.historySaver(wallet,trade.getId(),trade.getPrice(),"판매수익");
	}

	@Transactional(readOnly=true)
	public WalletResponse getWallet(UserAuth userAuth) {
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());
		Wallet wallet = walletRepository.findById(user.getId()).orElseThrow(()->new BizException(WalletErrorCode.NO_WALLET_FOUND));
		return new WalletResponse(wallet.getId(), user.getName(),wallet.getCash());
	}

}
