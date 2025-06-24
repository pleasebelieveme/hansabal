package org.example.hansabal.domain.review.repository;

import org.example.hansabal.domain.review.dto.response.ReviewSimpleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewCustomRepository {
    Page<ReviewSimpleResponse> findByProductId(Long productId, Pageable pageable);
}
