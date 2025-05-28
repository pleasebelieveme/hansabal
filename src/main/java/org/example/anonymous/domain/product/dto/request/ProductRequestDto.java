package org.example.anonymous.domain.product.dto.request;

public class ProductRequestDto {
    private String name;

    public ProductRequestDto() {
    }

    public ProductRequestDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
