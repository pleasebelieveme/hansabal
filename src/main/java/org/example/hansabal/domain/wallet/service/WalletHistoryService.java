package org.example.hansabal.domain.wallet.service;

import org.example.hansabal.domain.wallet.entity.Wallet;
import org.example.hansabal.domain.wallet.entity.WalletHistory;
import org.example.hansabal.domain.wallet.repository.WalletHistoryRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletHistoryService {

	// private final WalletHistoryRepository walletHistoryRepository;
	//
	// public void historySaver(Wallet wallet, Long tradeId, Long price){
	// 	WalletHistory walletHistory= WalletHistory.builder()
	// 		.walletId(wallet)
	// 		.tradeId(tradeId)
	// 		.price(price)
	// 		.remain(wallet.getCash()-price)
	// 		.build();
	// 	walletHistoryRepository.save(walletHistory);
	// }
}
