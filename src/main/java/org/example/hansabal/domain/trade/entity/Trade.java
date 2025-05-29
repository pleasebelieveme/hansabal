package org.example.hansabal.domain.trade.entity;

import org.example.hansabal.domain.users.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "trade")
public class Trade {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long tradeId;
	@Column(nullable=false)
	private String title;
	private String contents;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User trader;

	public Trade(String title, String contents, User trader){
		this.title=title;
		this.contents=contents;
		this.trader=trader;
	}
	public void updateTrade(String title, String contents){
		this.title=title;
		this.contents=contents;
	}
}
