package org.example.hansabal.domain.product.repository;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.product.entity.Product;
import org.example.hansabal.domain.review.exception.ReviewErrorCode;
import org.example.hansabal.domain.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom{

    default Product findByIdOrElseThrow(long id) {
        return findById(id).orElseThrow(() -> new BizException(ReviewErrorCode.RIVIEW_NOT_FOUND_PRODUCT));
    }
}