package org.example.hansabal.domain.payment.service;

import java.io.IOException;
import java.math.BigDecimal;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.email.service.MailService;
import org.example.hansabal.domain.payment.dto.request.RequestPay;
import org.example.hansabal.domain.payment.dto.request.PaymentCallbackRequest;
import org.example.hansabal.domain.payment.entity.PaymentStatus;
import org.example.hansabal.domain.payment.exception.PaymentErrorCode;
import org.example.hansabal.domain.wallet.entity.Wallet;
import org.example.hansabal.domain.wallet.entity.WalletHistory;
import org.example.hansabal.domain.wallet.exception.WalletErrorCode;
import org.example.hansabal.domain.wallet.repository.WalletHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PaymentService   {

	private final IamportClient iamportClient;
	private final WalletHistoryRepository historyRepository;
	private final MailService mailService;

	public RequestPay findRequestDto(String id) {

		WalletHistory history = historyRepository.findByUuid(id);

		return RequestPay.builder()
			.paymentPrice(history.getPrice())
			.uuid(history.getUuid())
			.buyerName(history.getWallet().getUser().getName())
			.buyerEmail(history.getWallet().getUser().getEmail())
			.build();
	}

	public IamportResponse<Payment> paymentByCallback(PaymentCallbackRequest request) {

		try {
			// 결제 단건 조회(아임포트)
			IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(request.getPaymentUid());
			log.info("🧾 아임포트 응답 imp_uid={}, amount={}, status={}",
				iamportResponse.getResponse().getImpUid(),
				iamportResponse.getResponse().getAmount(),
				iamportResponse.getResponse().getStatus());
			// 주문내역 조회
			WalletHistory history = historyRepository.findByUuid(request.getUuid());
			if (history==null){
				log.warn("❗️UUID에 해당하는 WalletHistory 없음: {}", request.getUuid());
				throw new BizException(WalletErrorCode.HISTORY_NOT_EXIST);
			}

			String status = iamportResponse.getResponse().getStatus();
			log.warn("⚠️ 결제 상태 검증: status={}", status);

			// 결제 완료가 아니면
			if (!"paid".equals(status)) {
				log.warn("❌ 결제 실패 상태로 응답됨: impUid={}, status={}", iamportResponse.getResponse().getImpUid(), status);
				history.softDelete();
				throw new BizException(PaymentErrorCode.LOAD_FAILED);
			}

			// DB에 저장된 결제 금액
			Long price = history.getPrice();
			// 실 결제 금액
			int iamportPrice = iamportResponse.getResponse().getAmount().intValue();

			log.info("💰 기대 금액={}, 실 결제 금액={}", price, iamportPrice);

			// 결제 금액 검증
			if(iamportPrice != price) {
				// 주문, 결제 삭제
				history.softDelete();
				// 결제금액 위변조로 의심되는 결제금액을 취소(아임포트)
				iamportClient.cancelPaymentByImpUid(new CancelData(iamportResponse.getResponse().getImpUid(), true, new BigDecimal(iamportPrice)));

				throw new BizException(PaymentErrorCode.SUSPICIOUS_VALUE_FOUND);
			}

			// 결제 상태 변경
			history.getPayment().changePaymentBySuccess(PaymentStatus.OK, iamportResponse.getResponse().getImpUid());
			//결제 완료점
			Wallet wallet = history.getWallet();
			wallet.updateWallet(wallet.getCash()+price);
			mailService.purchaseCompletedEmail(wallet.getUser().getName(),wallet.getUser().getEmail());
			return iamportResponse;

		} catch (IamportResponseException e) {
			log.error("❌ 아임포트 응답 실패", e);
			throw new BizException(PaymentErrorCode.LOAD_FAILED);
		} catch (IOException e) {
			log.error("❌ IO 예외 발생", e);
			throw new BizException(PaymentErrorCode.IOEXCEPTION_FOUND);
		}
	}
	// public void mailSender(){//메일서비스 연결 테스트용
	// 	User user = User.builder()
	// 		.id(1L)
	// 		.email("이메일")
	// 		.password("testpass")
	// 		.name("이름(실명)")
	// 		.nickname("별명")
	// 		.lastLoginAt(LocalDateTime.now())
	// 		.userRole(UserRole.USER)
	// 		.userStatus(UserStatus.ACTIVE)
	// 		.build();
	// 	mailService.purchaseCompletedEmail(user().getName(),user().getEmail());
	// }
}
