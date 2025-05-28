package org.example.anonymous.domain.product.dto.response;

import org.example.anonymous.domain.product.entity.CartItem;

public record ChangeQuantityResponse(
        int quantity
) {
    public static ChangeQuantityResponse from(CartItem cartItem){
        return new ChangeQuantityResponse(cartItem.getQuantity());
    }
}