package org.example.hansabal.domain.product.dto.response;

import org.example.hansabal.domain.product.entity.CartItem;

public record ItemResponse(
        String productName,
        int quantity
) {
    public static ItemResponse from(CartItem cartItem){
        return new ItemResponse(cartItem.getProduct().getName(),cartItem.getQuantity());
    }
}
