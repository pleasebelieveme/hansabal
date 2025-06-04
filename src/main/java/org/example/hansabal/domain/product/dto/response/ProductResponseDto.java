package org.example.hansabal.domain.product.dto.response;

import org.example.hansabal.domain.product.entity.Product;

public record ProductResponseDto(
        Long id,
        String name
) {
    public static ProductResponseDto from(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName()
        );
    }
}
