package org.example.hansabal.domain.product.entity;

import java.awt.*;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hansabal.common.base.BaseEntity;
import org.example.hansabal.domain.users.entity.User;

@Entity
@Table(name = "cart")
@NoArgsConstructor
@Getter
public class Cart extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int quantity;

    public Cart(User user, Product product, int quantity) {
        this.user = user;
        this.product =product;
        this.quantity = quantity;
    }

    public void updateCart(int quantity) {
        this.product =product;
        this.quantity = quantity;
    }
}