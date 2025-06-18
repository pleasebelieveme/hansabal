package org.example.hansabal.domain.batch.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.example.hansabal.domain.admin.entity.ProductOrderStatId;
import org.example.hansabal.domain.admin.entity.ProductOrderStatMonthly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ProductOrderStatMonthlyRepository
        extends JpaRepository<ProductOrderStatMonthly, ProductOrderStatId> {

    @Query("""
			SELECT s
			FROM ProductOrderStatMonthly s
			WHERE s.id.ProdcutId = :productId
			  AND s.id.date >= :from AND s.id.date < :to
		""")
    List<ProductOrderStatMonthly> findByProductIdAndDateRange(
            @Param("ProductId") Long ProductId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("""
			SELECT s
			FROM ProductOrderStatMonthly s
			WHERE s.id.date = :date
		""")
    List<ProductOrderStatMonthly> findAllByDate(@Param("date") LocalDate date);

}
