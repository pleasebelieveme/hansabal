package org.example.hansabal.domain.product.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.example.hansabal.domain.product.dto.response.ProductResponseDto;
import org.example.hansabal.domain.product.entity.QProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
        QProduct product = QProduct.product;

        List<ProductResponseDto> content = queryFactory
                .select(Projections.constructor(ProductResponseDto.class,
                        product.productId,
                        product.name))
                .from(product)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long countResult = queryFactory
                .select(product.productId.count())
                .from(product)
                .fetchOne();

        long total = (countResult != null) ? countResult : 0L;

        return new PageImpl<>(content, pageable, total);
        //실제데이터 리스트,페이지 정보,전체개수
    }
}