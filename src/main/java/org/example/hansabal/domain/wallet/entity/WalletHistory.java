package org.example.hansabal.domain.wallet.entity;

import org.example.hansabal.common.base.BaseEntity;
import org.example.hansabal.domain.payment.entity.Payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "history")
public class WalletHistory extends BaseEntity {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wallet_id")
	private Wallet wallet;
	private String type;
	@Column(name = "trade_id")
	private Long tradeId;
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "payment")
	private Payment payment;
	@Column(unique = true)
	private String uuid;
	private Long price;//변동액(+/-)
	private Long remain;//잔액
}
