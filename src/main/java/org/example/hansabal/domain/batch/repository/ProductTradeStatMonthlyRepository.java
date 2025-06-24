package org.example.hansabal.domain.batch.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.example.hansabal.domain.admin.entity.ProductTradeStatId;
import org.example.hansabal.domain.admin.entity.ProductTradeStatMonthly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ProductTradeStatMonthlyRepository
        extends JpaRepository<ProductTradeStatMonthly, ProductTradeStatId> {

    @Query("""
			SELECT s
			FROM ProductTradeStatMonthly s
			WHERE s.id.ProdcutId = :productId
			  AND s.id.date >= :from AND s.id.date < :to
		""")
    List<ProductTradeStatMonthly> findByProductIdAndDateRange(
            @Param("ProductId") Long ProductId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("""
			SELECT s
			FROM ProductTradeStatMonthly s
			WHERE s.id.date = :date
		""")
    List<ProductTradeStatMonthly> findAllByDate(@Param("date") LocalDate date);

}
