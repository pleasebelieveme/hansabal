package org.example.hansabal.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product")
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus productStatus;

    public Product(String name, int quantity, ProductStatus productStatus) {
        this.name = name;
        this.quantity = quantity;
        this.productStatus = productStatus;
    }
    public static Product of(String name, int quantity) {
        return new Product(name, quantity, ProductStatus.FOR_SALE);
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public Product(ProductStatus productStatus) {
        this.productStatus = productStatus;
    }
    public Long getId() {
        return this.productId;
    }

}
