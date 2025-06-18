package org.example.hansabal.domain.batch.repository;

import org.springframework.data.repository.query.Param;
import org.example.hansabal.domain.admin.entity.ProductOrderStatDaily;
import org.example.hansabal.domain.admin.entity.ProductOrderStatId;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface ProductOrderStatDailyRepository extends JpaRepository<ProductOrderStatDaily, ProductOrderStatId> {
        @Query("""
			SELECT s
			FROM ProductOrderStatDaily s
			WHERE s.id.ProdcutId = :productId
			  AND s.id.date >= :from AND s.id.date < :to
		""")
        List<ProductOrderStatDaily> findAllByProductIdAndDateRange(
            @Param("productId") Long productId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
	);

        @Query("""
			SELECT s
			FROM ProductOrderStatDaily s
			WHERE s.id.date >= :from AND s.id.date < :to
		""")
        List<ProductOrderStatDaily> findAllByDateRange(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
	);

        @Modifying
        @Query("""
			UPDATE ProductOrderStatDaily s
			SET s.orderCount = :orderCount,
				s.totalSales = :totalSales
			WHERE s.id = :id
		""")
        void bulkUpdate(ProductOrderStatId id, Integer orderCount, Long totalSales);
}
