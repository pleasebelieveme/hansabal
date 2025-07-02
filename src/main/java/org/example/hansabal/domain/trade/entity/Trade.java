package org.example.hansabal.domain.trade.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hansabal.common.base.BaseEntity;
import org.example.hansabal.domain.product.entity.Product;
import org.example.hansabal.domain.users.entity.User;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "trade")
public class Trade extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	private String contents;

	private Long price;

	@Builder.Default
	@Column(columnDefinition = "tinyint(1) default 0")
	private boolean isOccupied = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User trader;


	public void updateTrade(String title, String contents, Long price) {
		this.title = title;
		this.contents = contents;
		this.price = price;
	}

	public boolean getIsOccupied() {
		return this.isOccupied;
	}

	public void occupiedCheck(boolean b) {
		this.isOccupied = b;
	}


}
