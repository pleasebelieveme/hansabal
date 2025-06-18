package org.example.hansabal.domain.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hansabal.common.base.BaseEntity;
import org.example.hansabal.domain.product.entity.Product;
import org.example.hansabal.domain.users.entity.User;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Integer totalPrice;

	@Column(nullable = false)
	private OrderStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Product_id")
	private Product product;

	public Order(Integer totalPrice, User user, Product product) {
		this.totalPrice = totalPrice;
		this.user = user;
		this.product = product;
	}

	public Order(Integer totalPrice, OrderStatus orderStatus, User user,Product product) {
		this.totalPrice = totalPrice;
		this.status = orderStatus;
		this.user = user;
		this.product = product;
	}


	public void updateStatus(OrderStatus orderStatus) {
		this.status = orderStatus;
	}
}
