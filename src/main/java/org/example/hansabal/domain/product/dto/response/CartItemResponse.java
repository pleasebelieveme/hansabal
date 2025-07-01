package org.example.hansabal.domain.product.dto.response;


import org.example.hansabal.domain.product.entity.Cart;

public record CartItemResponse(
	Long id,
	Long ProductId,
	String product,
	int quantity
) {
	public static CartItemResponse from(Cart cart) {
		return new CartItemResponse(
			cart.getId(),
			cart.getProduct().getId(),
			cart.getProduct().getName(),
			cart.getQuantity()
		);
	}
}
