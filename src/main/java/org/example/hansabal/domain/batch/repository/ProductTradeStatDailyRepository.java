package org.example.hansabal.domain.batch.repository;

import org.springframework.data.repository.query.Param;
import org.example.hansabal.domain.admin.entity.ProductTradeStatDaily;
import org.example.hansabal.domain.admin.entity.ProductTradeStatId;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface ProductTradeStatDailyRepository extends JpaRepository<ProductTradeStatDaily, ProductTradeStatId> {
        @Query("""
			SELECT s
			FROM ProductTradeStatDaily s
			WHERE s.id.productId = :productId
			  AND s.id.date >= :from AND s.id.date < :to
		""")
        List<ProductTradeStatDaily> findAllByProductIdAndDateRange(
            @Param("productId") Long productId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
	);

        @Query("""
			SELECT s
			FROM ProductTradeStatDaily s
			WHERE s.id.date >= :from AND s.id.date < :to
		""")
        List<ProductTradeStatDaily> findAllByDateRange(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
	);

        @Modifying
        @Query("""
			UPDATE ProductTradeStatDaily s
			SET s.TradeCount = :TradeCount,
				s.totalSales = :totalSales
			WHERE s.id = :id
		""")
        void bulkUpdate(ProductTradeStatId id, Integer TradeCount, Long totalSales);
}
