package org.example.hansabal.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hansabal.common.base.BaseEntity;
import org.example.hansabal.domain.users.entity.User;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product")
@Entity
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus productStatus;

    public Product(String name, int quantity,int price ,User user, ProductStatus productStatus) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.user = user;
        this.productStatus = productStatus;
    }
    public static Product of(String name, int quantity,User user, int price) {
        return new Product(name, quantity, price, user, ProductStatus.FOR_SALE);
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updatePrice(int price) {
        this.price = price;
    }
}
