package org.example.hansabal.domain.order.entity;


import jakarta.persistence.*;
import lombok.Getter;
import org.example.hansabal.domain.product.entity.Product;

import java.awt.*;

@Getter
@Entity
@Table(name = "order_items")
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Integer quantity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	public OrderItem(int quantity, Order order, Product product ){
		this.quantity = quantity;
		this.order = order;
		this.product = product;
	}

	public OrderItem() {

	}
}
