package org.example.hansabal.domain.product.dto.response;


import org.example.hansabal.domain.product.entity.Cart;

public record CartCreateResponse(
	Long id,
	String product,
	int quantity
) {
	public static CartCreateResponse from(Cart cart) {
		return new CartCreateResponse(
			cart.getId(),
			cart.getProduct().getName(),
			cart.getQuantity()
		);
	}
}
