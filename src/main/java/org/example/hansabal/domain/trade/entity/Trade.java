package org.example.hansabal.domain.trade.entity;

import org.example.hansabal.common.base.BaseEntity;
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
public class Trade extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable=false)
	private String title;
	private String contents;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User trader;
	private Long price;
	@Builder.Default
	@Column(columnDefinition = "tinyint(1) default 0")
	private boolean isOccupied = false;


	public void updateTrade(String title, String contents, Long price){
		this.title=title;
		this.contents=contents;
		this.price=price;
	}
	public void occupiedCheck(boolean isOccupied){
		this.isOccupied=isOccupied;
	}

	public boolean getIsOccupied() {//@Getter 바로 적용 안되서 임시로 작성, 이후 삭제예정...
		return isOccupied;
	}
}
