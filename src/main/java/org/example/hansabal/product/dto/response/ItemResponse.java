package org.example.hansabal.product.dto.response;

import org.example.hansabal.product.entity.CartItem;

public record ItemResponse(
        String ProductName,
        int quantity
) {
    public static ItemResponse from(CartItem cartItem){
        return new ItemResponse(cartItem.getProduct().getName(),cartItem.getQuantity());
    }
}
