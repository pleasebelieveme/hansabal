package org.example.hansabal.domain.product.repository;

import org.example.hansabal.domain.product.dto.response.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProductRepositoryCustom {
    Page<ProductResponseDto> getAllProducts(Long productId, Pageable pageable);
}
