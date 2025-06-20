package org.example.hansabal.domain.payment.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RequestPay {
	private String uuid;
	private Long paymentPrice;
	private String buyerName;
	private String buyerEmail;

	@Builder
	public RequestPay(String uuid,Long paymentPrice, String buyerName, String buyerEmail) {
		this.uuid = uuid;
		this.paymentPrice = paymentPrice;
		this.buyerName = buyerName;
		this.buyerEmail = buyerEmail;
	}
}
