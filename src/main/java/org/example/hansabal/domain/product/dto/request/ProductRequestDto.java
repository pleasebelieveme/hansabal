package org.example.hansabal.domain.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequestDto(
        @NotBlank(message = "상품 이름은 필수입니다.")
        String name,

        @NotNull(message = "상품 가격은 필수입니다.")
        @Min(value = 0, message = "상품 가격은 0 이상이어야 합니다.")
        Integer price,

        @NotNull(message = "상품 수량은 필수입니다.")
        @Min(value = 0, message = "상품 수량은 0 이상이어야 합니다.")
        Integer quantity
) {
}