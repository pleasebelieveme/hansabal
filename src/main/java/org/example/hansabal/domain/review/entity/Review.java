package org.example.hansabal.domain.review.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hansabal.common.base.BaseEntity;
import org.example.hansabal.domain.product.entity.Product;
import org.example.hansabal.domain.users.entity.User;

import java.util.ArrayList;


@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor
public class Review extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    @Lob //text 타입
    private String content;

    @Column(nullable = false)
    private Integer rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    public Review(String content, Integer rating, User user, Product product) {
        this.content = content;
        this.rating = rating;
        this.user = user;
        this.product = product;
    }

    public void updateReview(String updateContent) {
        this.content = updateContent;
    }

}
