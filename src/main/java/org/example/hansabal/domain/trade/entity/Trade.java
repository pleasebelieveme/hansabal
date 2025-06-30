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

	@Column(nullable = false)
	private Long price;

	@Column(nullable = false)
	private Integer totalPrice;

	@Builder.Default
	@Column(columnDefinition = "tinyint(1) default 0")
	private boolean isOccupied = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "writer_id")
	private User writer; // 게시글 작성자

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TradeStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RequestStatus restatus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user; // 주문자(필요시)

	public void updateTrade(String title, String contents, Long price) {
		this.title = title;
		this.contents = contents;
		this.price = price;
	}

	public void setOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;
	}

	public boolean getIsOccupied() {
		return this.isOccupied;
	}

	public void occupiedCheck(boolean b) {
		this.isOccupied = b;
	}

	public User getTrader() {
		return this.writer;
	}


}
