package org.example.hansabal.domain.product.dto.request;

public record CartCreateRequest(
	Long storeId,
	Long menuId,
	Long menuOptionId,
	int quantity
) {
}
