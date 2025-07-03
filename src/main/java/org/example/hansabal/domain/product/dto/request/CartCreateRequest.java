package org.example.hansabal.domain.product.dto.request;

public record CartCreateRequest(
	Long productId,
	int quantity
) {
}
