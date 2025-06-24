package org.example.hansabal.domain.review.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.product.entity.QProduct;
import org.example.hansabal.domain.review.dto.response.ReviewSimpleResponse;
import org.example.hansabal.domain.review.entity.QReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.expression.spel.ast.Projection;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReviewSimpleResponse> findByProductId(Long productId, Pageable pageable) {

        QReview review = QReview.review;
        QProduct product = QProduct.product;

        List<ReviewSimpleResponse> results = queryFactory
                .select(Projections.constructor(
                        ReviewSimpleResponse.class,
                        review.user.nickname,review.content,review.rating
                ))
                .from(review)
                .join(review.product)
                .where(product.id.eq(productId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = Optional.ofNullable(queryFactory
                .select(review.count())
                .from(review)
                .where(product.id.eq(productId))
                .fetchOne()).orElse(0L) ;
        //todo GPT로 공부

        return new PageImpl<>(results, pageable, total);
    }
}
