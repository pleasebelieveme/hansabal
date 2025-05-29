package org.example.hansabal.domain.review.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hansabal.common.base.BaseEntity;
import org.example.hansabal.domain.product.entity.Product;
import org.example.hansabal.domain.users.entity.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor
public class Review extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

//       아직 엔티티가 없어서 주석처리하였습니다
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

//    아직 엔티티가 없어서 주석처리하였습니다
//    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL ,orphanRemoval = true)
//    private List<Div> divs = new ArrayList<>();


    public Review(String content, User user, Product product) {
        this.content = content;
        this.user = user;
        this.product = product;
    }

    public void updateReview(String updateContent) {
        this.content = updateContent;
    }
}
