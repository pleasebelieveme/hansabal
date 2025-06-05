package org.example.hansabal.domain.wallet.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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
@Table(name = "wallet_history")
public class WalletHistory {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private Wallet walletId;
	private Long tradeId;
	private Long price;//변동액(+/-)
	private Long remain;//잔액

	public WalletHistory(Wallet walletId, Long tradeId, Long price, Long remain){
		this.walletId=walletId;
		this.tradeId=tradeId;
		this.price=price;
		this.remain=remain;
	}
}
