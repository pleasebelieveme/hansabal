package org.example.hansabal.domain.wallet.entity;

import org.example.hansabal.common.base.BaseEntity;
import org.example.hansabal.domain.users.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "trade")
public class Wallet extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@OneToOne
	@JoinColumn(name="users_id", unique=true)
	private User userId;
	private Long cash;
	public Wallet(User userId, Long cash){
		this.userId=userId;
		this.cash=cash;
	}
	public void updateWallet(Long cash){
		this.cash=cash;
	}
}
