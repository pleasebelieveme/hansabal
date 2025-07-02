package org.example.hansabal.domain.trade.entity;

import org.example.hansabal.common.base.BaseEntity;
import org.example.hansabal.domain.users.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "requests")
public class Requests extends BaseEntity {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private RequestStatus status;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="trade_id")
	private Trade trade;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="users_id")
	private User requester;

	public Requests(RequestStatus status, Trade trade, User requester){
		this.status=status;
		this.trade=trade;
		this.requester=requester;
	}
	public static Requests of(Trade trade, User requester){
		return new Requests(RequestStatus.AVAILABLE,trade, requester);
	}
	public void updateStatus(RequestStatus status){
		this.status=status;
	}
}
