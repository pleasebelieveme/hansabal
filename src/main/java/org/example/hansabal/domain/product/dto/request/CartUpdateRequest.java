package org.example.hansabal.domain.product.dto.request;

public record CartUpdateRequest(
        String product,
        int quantity
) {
}
