package org.example.hansabal.domain.wallet.service;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.trade.entity.Requests;
import org.example.hansabal.domain.trade.entity.Trade;
import org.example.hansabal.domain.trade.repository.RequestsRepository;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.example.hansabal.domain.wallet.dto.request.ChargeRequestDto;
import org.example.hansabal.domain.wallet.dto.response.WalletResponseDto;
import org.example.hansabal.domain.wallet.entity.Wallet;
import org.example.hansabal.domain.wallet.exception.WalletErrorCode;
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

	@Transactional
	public void createWallet(UserAuth userAuth) {
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());
		if(walletRepository.existsByUserId(user))
			throw new BizException(WalletErrorCode.DUPLICATE_WALLET_NOT_ALLOWED);
		Wallet wallet =  Wallet.builder()
			.userId(user)
			.cash(0L)
			.build();
		walletRepository.save(wallet);
	}

	@Transactional
	public WalletResponseDto chargeWallet(ChargeRequestDto request, UserAuth userAuth) {
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());
		Wallet wallet = walletRepository.findById(request.id()).orElseThrow(()->new BizException(WalletErrorCode.NO_SUCH_THING));
		//연결 후 구현 예정 -ㅅ-(실제 결제가 일어나는 구간)
		wallet.updateWallet(wallet.getCash()+request.cash());
		return new WalletResponseDto(user.getName(),wallet.getCash());
	}

	@Transactional(propagation= Propagation.REQUIRES_NEW)
	public void walletPay(User user, Long requestsId, Long price){
		Wallet wallet = walletRepository.findByUserId(user.getId()).orElseThrow(()->new BizException(WalletErrorCode.NO_SUCH_THING));
		wallet.updateWallet(wallet.getCash()-price);
		//History구간, 나중에 구현예정, requestId는 여기에서 쓰입니당
	}

	@Transactional(propagation= Propagation.REQUIRES_NEW)
	public void walletConfirm(Trade trade, Long requestsId) {
		requestsRepository.findById(requestsId).orElseThrow(()->new BizException(WalletErrorCode.NO_SUCH_THING));
		User trader= trade.getTrader();
		Wallet wallet = walletRepository.findByUserId(trader.getId()).orElseThrow(()->new BizException(WalletErrorCode.NO_SUCH_THING));
		wallet.updateWallet(wallet.getCash()+trade.getPrice());
		//History구간, 나중에 구현예정
	}

	@Transactional(readOnly=true)
	public WalletResponseDto getWallet(UserAuth userAuth) {
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());
		Wallet wallet = walletRepository.findById(user.getId()).orElseThrow(()->new BizException(WalletErrorCode.NO_SUCH_THING));
		return new WalletResponseDto(user.getName(),wallet.getCash());
	}

}
