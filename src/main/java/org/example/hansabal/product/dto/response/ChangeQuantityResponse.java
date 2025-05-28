package org.example.hansabal.product.dto.response;

import org.example.hansabal.product.entity.CartItem;

public record ChangeQuantityResponse(
        int quantity
) {
    public static ChangeQuantityResponse from(CartItem cartItem){
        return new ChangeQuantityResponse(cartItem.getQuantity());
    }
}