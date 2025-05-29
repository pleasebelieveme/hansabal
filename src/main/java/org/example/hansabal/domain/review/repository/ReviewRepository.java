package org.example.hansabal.domain.review.repository;

import org.example.hansabal.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

}
