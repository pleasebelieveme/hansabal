package org.example.hansabal.domain.product.dto.response;

import org.example.hansabal.domain.product.entity.CartItem;

public record CartItemResponse(
		Long id,
		Long productId,
		String productName,
		int quantity
) {
	public static CartItemResponse from(CartItem cartItem) {
		return new CartItemResponse(
				cartItem.getId(),
				cartItem.getProduct().getId(),
				cartItem.getProduct().getName(),
				cartItem.getQuantity()
		);
	}
}
