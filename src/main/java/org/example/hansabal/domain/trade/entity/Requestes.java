package org.example.hansabal.domain.trade.entity;

import org.example.hansabal.domain.users.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "requestes")
public class Requestes {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long requestId;
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private RequestStatus status;
	@ManyToOne
	@JoinColumn(name="trade_id")
	private Trade tradeId;
	@ManyToOne
	@JoinColumn(name="user_id")
	private User requesterId;

	public Requestes(RequestStatus status, Trade tradeId, User requesterId){
		this.status=status;
		this.tradeId=tradeId;
		this.requesterId=requesterId;
	}
	public static Requestes of(Trade tradeId, User requesterId){
		return new Requestes(RequestStatus.AVAILABLE,tradeId, requesterId);
	}
	public void updateStatus(RequestStatus status){
		this.status=status;
	}
}
