package org.example.hansabal.product.dto.response;

public class ProductResponseDto {

    private Long productId;
    private String name;

    public ProductResponseDto(Long productId, String name) {
        this.productId = productId;
        this.name = name;
    }

    public Long getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }
}
