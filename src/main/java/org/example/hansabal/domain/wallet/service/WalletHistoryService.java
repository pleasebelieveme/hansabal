package org.example.hansabal.domain.wallet.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.payment.entity.Payment;
import org.example.hansabal.domain.payment.exception.PaymentErrorCode;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.exception.UserErrorCode;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.example.hansabal.domain.wallet.dto.request.LoadRequest;
import org.example.hansabal.domain.wallet.dto.response.HistoryResponse;
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

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletHistoryService {

	private final WalletHistoryRepository walletHistoryRepository;
	private final WalletRepository walletRepository;
	private final UserRepository userRepository;

	@Transactional
	public void historySaver(Wallet wallet, Long tradeId, Long price, String type, Long remain) {//일반 기록 저장
		WalletHistory walletHistory = WalletHistory.builder()
			.wallet(wallet)
			.type(type)
			.tradeId(tradeId)
			.price(price)
			.remain(remain)
			.build();
		walletHistoryRepository.save(walletHistory);
		log.info("✅ saved new walletHistory: {}, tradeId={}, type={}", walletHistory, tradeId, type);
	}

	@Transactional//중요 : uuid의 통신시 UTF-8 형식 권장. 아닐경우 uuid 크기를 40Byte로 제한해야할 필요가 있음 -> PG사가 받는 merchant_uid의 크기 제한.
	public String historyLoadSaver(Wallet wallet, Long price, Payment payment){//결제(충전) 관련 기록 저장
		String uuidAddDate = UUID.randomUUID().toString().replace("-","")+ LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
		WalletHistory walletHistory = WalletHistory.builder()
			.wallet(wallet)
			.type("충전")
			.tradeId(0L)
			.payment(payment)
			.price(price)
			.remain(wallet.getCash()+price)
			.uuid(uuidAddDate)
			.build();
		walletHistoryRepository.save(walletHistory);
		return walletHistory.getUuid();
	}

	@Transactional(readOnly=true)
	public Page<HistoryResponse> getHistory(int page, int size, UserAuth userAuth) {
		User user = userRepository.findById(userAuth.getId()).orElseThrow(()->new BizException(UserErrorCode.NOT_FOUND_USER));
		Wallet wallet = walletRepository.findByUser(user).orElseThrow(()->new BizException(WalletErrorCode.NO_WALLET_FOUND));
		int pageIndex = Math.max(page - 1 , 0);
		Pageable pageable = PageRequest.of(pageIndex,size);
		return walletHistoryRepository.findByWalletIdOrderByCreatedAtDesc(pageable, wallet.getId());
	}

	@Transactional(readOnly = true)
	public LoadRequest getLoadRequestDto(String uuid, UserAuth userAuth, Long cash) {
		WalletHistory history = walletHistoryRepository.findByUuid(uuid);
		if (history == null) {
			throw new BizException(WalletErrorCode.HISTORY_NOT_EXIST);
		}

		Wallet wallet = history.getWallet();

		if (!wallet.getUser().getId().equals(userAuth.getId())) {
			throw new BizException(WalletErrorCode.INVALID_ACCESS);
		}

		if(Objects.isNull(cash)||Objects.isNull(history.getPrice()))
			throw new BizException(WalletErrorCode.INCORRECT_VALUE_FOUND);
		if(!history.getPrice().equals(cash))
			throw new BizException(PaymentErrorCode.SUSPICIOUS_VALUE_FOUND);

		return new LoadRequest(wallet.getId(), history.getPrice());
	}
}
