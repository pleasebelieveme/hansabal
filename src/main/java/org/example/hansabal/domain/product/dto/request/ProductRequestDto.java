package org.example.hansabal.domain.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductRequestDto {

        @NotBlank(message = "상품 이름은 필수입니다.")
        private String name;

        @NotNull(message = "상품 가격은 필수입니다.")
        @Min(value = 0, message = "상품 가격은 0 이상이어야 합니다.")
        private Integer price;

        @NotNull(message = "상품 수량은 필수입니다.")
        @Min(value = 0, message = "상품 수량은 0 이상이어야 합니다.")
        private Integer quantity;

        public ProductRequestDto(String name, Integer price, Integer quantity) {
                this.name = name;
                this.price = price;
                this.quantity = quantity;
        }
}
