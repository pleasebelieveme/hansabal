package org.example.hansabal.domain.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "payment")
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long price;
	private PaymentStatus status;
	private String paymentUid; // 결제 고유 번호

	@Builder
	public Payment(Long price, PaymentStatus status) {
		this.price = price;
		this.status = status;
	}

	public void changePaymentBySuccess(PaymentStatus status, String paymentUid) {
		this.status = status;
		this.paymentUid = paymentUid;
	}
}
