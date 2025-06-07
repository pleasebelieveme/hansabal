package org.example.hansabal.domain.wallet.service;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
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
	public void historySaver(Wallet wallet, Long tradeId, Long price){//Wallet 작동시 기록 저장(인덱싱 필요-이걸로 5분발표해야징 =ㅅ=)
		if(tradeId==0){
			WalletHistory walletHistory= WalletHistory.builder()
				.walletId(wallet)
				.tradeId(tradeId)
				.price(price)
				.remain(wallet.getCash()+price)
				.build();
			walletHistoryRepository.save(walletHistory);
		}
		else{
		WalletHistory walletHistory= WalletHistory.builder()
			.walletId(wallet)
			.tradeId(tradeId)
			.price(price)
			.remain(wallet.getCash()-price)
			.build();
		walletHistoryRepository.save(walletHistory);
		}
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
