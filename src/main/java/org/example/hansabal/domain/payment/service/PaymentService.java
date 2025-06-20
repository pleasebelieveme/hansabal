package org.example.hansabal.domain.payment.service;

import java.io.IOException;
import java.math.BigDecimal;

import org.example.hansabal.common.exception.BizException;
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

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService   {

	private final IamportClient iamportClient;
	private final WalletHistoryRepository historyRepository;

	public RequestPay findRequestDto(String id) {

		WalletHistory history = historyRepository.findByUuid(id);

		return RequestPay.builder()
			.paymentPrice(history.getPrice())
			.uuid(history.getUuid())
			.buyerName(history.getWallet().getUserId().getName())
			.buyerEmail(history.getWallet().getUserId().getEmail())
			.build();
	}

	public IamportResponse<Payment> paymentByCallback(PaymentCallbackRequest request) {

		try {
			// 결제 단건 조회(아임포트)
			IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(request.getPaymentUid());
			// 주문내역 조회
			WalletHistory history = historyRepository.findByUuid(request.getUuid());
			if(history==null)
				throw new BizException(WalletErrorCode.HISTORY_NOT_EXIST);

			// 결제 완료가 아니면
			if(!iamportResponse.getResponse().getStatus().equals("paid")) {
				history.softDelete();// 기록 삭제
				throw new BizException(PaymentErrorCode.LOAD_FAILED);
			}

			// DB에 저장된 결제 금액
			Long price = history.getPrice();
			// 실 결제 금액
			int iamportPrice = iamportResponse.getResponse().getAmount().intValue();

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
			return iamportResponse;

		} catch (IamportResponseException e) {
			throw new BizException(PaymentErrorCode.LOAD_FAILED);
		} catch (IOException e) {
			throw new BizException(PaymentErrorCode.IOEXCEPTION_FOUND);
		}
	}
}
