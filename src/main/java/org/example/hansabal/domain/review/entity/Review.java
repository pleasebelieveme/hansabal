package org.example.hansabal.domain.review.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hansabal.common.base.BaseEntity;
import org.example.hansabal.domain.product.entity.Product;
import org.example.hansabal.domain.users.entity.User;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor
public class Review extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false, columnDefinition = "LONGTEXT") // >LONGTEXT 타입이 용량 검색 인덱스 부분에서 더 효율적인
    private String content;

    @Column(nullable = false)
    private Integer rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public Review(String content, Integer rating, User user, Product product) {
        this.content = content;
        this.rating = rating;
        this.user = user;
        this.product = product;
    }

    public void updateReview(String updateContent, Integer rating) {
        this.content = updateContent;
        this.rating = rating;
    }
}
