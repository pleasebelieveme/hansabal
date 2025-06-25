package org.example.hansabal.domain.payment.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PaymentCallbackRequest {
	private String paymentUid; // 결제 고유 번호, imp_uid
	private String uuid; // 주문 고유 번호, imp_merchant
}
