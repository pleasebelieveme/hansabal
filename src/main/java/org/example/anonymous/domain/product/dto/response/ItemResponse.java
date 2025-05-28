package org.example.anonymous.domain.product.dto.response;

import org.example.anonymous.domain.product.entity.CartItem;

public record ItemResponse(
        String ProductName,
        int quantity
) {
    public static ItemResponse from(CartItem cartItem){
        return new ItemResponse(cartItem.getProduct().getName(),cartItem.getQuantity());
    }
}
