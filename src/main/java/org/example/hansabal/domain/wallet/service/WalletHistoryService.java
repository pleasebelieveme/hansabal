package org.example.hansabal.domain.wallet.service;

import java.util.UUID;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.payment.entity.Payment;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.exception.UserErrorCode;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.example.hansabal.domain.wallet.dto.response.HistoryResponseDto;
import org.example.hansabal.domain.wallet.entity.Wallet;
import org.example.hansabal.domain.wallet.entity.WalletHistory;
import org.example.hansabal.domain.wallet.exception.WalletErrorCode;
import org.example.hansabal.domain.wallet.repository.WalletHistoryRepository;
import org.example.hansabal.domain.wallet.repository.WalletRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletHistoryService {

	private final WalletHistoryRepository walletHistoryRepository;
	private final WalletRepository walletRepository;
	private final UserRepository userRepository;

	@Transactional(propagation= Propagation.REQUIRES_NEW)
	public void historySaver(Wallet wallet, Long tradeId, Long price, String type) {//tradeId가 0이라면 uid=imp_uid, 아닐경우 type 지정자
		WalletHistory walletHistory = WalletHistory.builder()
			.wallet(wallet)
			.type(type)
			.tradeId(tradeId)
			.price(price)
			.remain(wallet.getCash() - price)
			.build();
		walletHistoryRepository.save(walletHistory);
	}

	@Transactional
	public String historyChargeSaver(Wallet wallet, Long price, Payment payment){
		WalletHistory walletHistory = WalletHistory.builder()
			.wallet(wallet)
			.type("충전")
			.tradeId(0L)
			.payment(payment)
			.price(price)
			.remain(wallet.getCash()+price)
			.uuid(UUID.randomUUID().toString())
			.build();
		walletHistoryRepository.save(walletHistory);
		return walletHistory.getUuid();
	}

	@Transactional(readOnly=true)
	public Page<HistoryResponseDto> getHistory(int page, int size, UserAuth userAuth) {
		User user = userRepository.findById(userAuth.getId()).orElseThrow(()->new BizException(UserErrorCode.NOT_FOUND_USER));
		Wallet wallet = walletRepository.findByUserId(user).orElseThrow(()->new BizException(WalletErrorCode.NO_WALLET_FOUND));
		int pageIndex = Math.max(page - 1 , 0);
		Pageable pageable = PageRequest.of(pageIndex,size);
		Page<WalletHistory> walletHistory = walletHistoryRepository.findByWalletIdOrderByCreatedAtDesc(pageable, wallet);
		return walletHistory.map(HistoryResponseDto::from);
	}
}
